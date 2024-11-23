/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.logger

import zernikalos.settings.ZLoggerSettings
import zernikalos.settings.ZSettings
import kotlin.js.JsExport

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
    private val settings: ZLoggerSettings = ZSettings.getInstance().loggerSettings

    fun debug(message: String) {
        fireMessage(message, ZLogLevel.DEBUG)
    }

    fun debugOnce(message: String) {
        if (message in logOnceSet) {
            return
        }
        logOnceSet.add(message)
        fireMessage(message, ZLogLevel.DEBUG)
    }

    fun info(message: String) {
        fireMessage(message, ZLogLevel.INFO)
    }

    fun warn(message: String) {
        fireMessage(message, ZLogLevel.WARNING)
    }

    fun error(message: String) {
        fireMessage(message, ZLogLevel.ERROR)
    }

    private fun fireMessage(message: String, level: ZLogLevel) {
        if (level < settings.logLevel) {
            return
        }
        val m = buildMessage(message, level)
        settings.adapters.forEach { adapter ->
            when (level) {
                ZLogLevel.DEBUG -> adapter.debug(m)
                ZLogLevel.INFO -> adapter.info(m)
                ZLogLevel.WARNING -> adapter.warn(m)
                ZLogLevel.ERROR -> adapter.error(m)
            }
        }
    }

    private fun buildMessage(message: String, level: ZLogLevel): String {
        return "${prettyLogLevel(level.name)} [$clsName] $message"
    }

    private fun prettyLogLevel(levelName: String): String {
        val max = ZLogLevel.entries.map { it.name }.maxBy { it.length }
        val extraSpaces = max.length - levelName.length
        return "[Zernikalos - ${levelName}]${" ".repeat(extraSpaces)}"
        }

    companion object {

        private val instances: HashMap<Int, ZLogger> = hashMapOf()

        fun getLogger(clsName: String?, instanceId: Int): ZLogger {
            if (instances.containsKey(instanceId)) {
                return instances.getValue(instanceId)
            }
            val logger = ZLogger(clsName, instanceId)
            instances[instanceId] = logger
            return logger
        }
    }

}