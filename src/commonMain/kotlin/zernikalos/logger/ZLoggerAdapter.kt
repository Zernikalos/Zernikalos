package zernikalos.logger

import kotlin.js.JsExport

@JsExport
interface ZLoggerAdapter {
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String)
}

expect class ZLoggerAdapterConsole(): ZLoggerAdapter {
    override fun debug(message: String)
    override fun info(message: String)
    override fun warn(message: String)
    override fun error(message: String)
}