package zernikalos.settings

import zernikalos.logger.ZLoggerAdapter
import zernikalos.logger.ZLoggerAdapterConsole
import zernikalos.logger.ZLogLevel
import kotlin.js.JsExport

@JsExport
class ZLoggerSettings {
    val debug: Boolean = true
    var adapters: Array<ZLoggerAdapter> = emptyArray()
    var logLevel: ZLogLevel = ZLogLevel.INFO

    init {
        addAdapter(ZLoggerAdapterConsole())
    }

    fun addAdapter(adapter: ZLoggerAdapter) {
        adapters += adapter
    }
}