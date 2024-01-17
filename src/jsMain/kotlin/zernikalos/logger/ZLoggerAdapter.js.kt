package zernikalos.logger

import kotlin.js.Console

abstract external class WindowConsole: Console {
    fun debug(vararg o: Any?)

}

external val console: WindowConsole

actual class ZLoggerAdapterConsole: ZLoggerAdapter() {
    override fun debug(message: String) {
        console.debug(message)
    }

}