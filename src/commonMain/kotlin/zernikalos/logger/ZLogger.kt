package zernikalos.logger

import kotlin.js.JsExport

@JsExport
interface ZLoggable {

}

inline fun <reified T: ZLoggable> T.createLogger(): ZLogger {
    return ZLogger(T::class.simpleName, this.hashCode())
}

inline fun <reified T: ZLoggable> T.createLazyLogger(): Lazy<ZLogger> {
    return lazy { ZLogger(T::class.simpleName, this.hashCode()) }
}

inline val <reified T: ZLoggable> T.logger: ZLogger
    get() {
        return ZLogger.getLogger(T::class.simpleName, this.hashCode())
    }

@JsExport
class ZLogger(private val clsName: String?, val instanceId: Int) {

    private val logOnceSet = HashSet<String>()
    private val adapters = ArrayList<ZLoggerAdapter>()

    init {
        adapters.add(ZLoggerAdapterConsole())
    }

    fun debug(message: String) {
        debugMessage(message)
    }

    fun debugOnce(message: String) {
        if (message in logOnceSet) {
            return
        }
        logOnceSet.add(message)
        debugMessage(message)
    }

    private fun debugMessage(message: String) {
        val m = buildMessage(message)
        adapters.forEach {
            it.debug(m)
        }
    }

    private fun buildMessage(message: String): String {
        return "[$clsName] $message"
    }

    companion object {

        private val instances: HashMap<Int, ZLogger> = hashMapOf()

        fun getLogger(clsName: String?, instanceId: Int): ZLogger {
            if (instances.containsKey(instanceId)) {
                return instances.getValue(instanceId)
            }
            val logger = ZLogger(clsName, instanceId)
            instances.set(instanceId, logger)
            return logger
        }
    }

}