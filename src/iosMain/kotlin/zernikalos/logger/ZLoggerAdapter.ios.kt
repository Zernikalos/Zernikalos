package zernikalos.logger

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ptr
import platform.darwin.*

actual class ZLoggerAdapterConsole : ZLoggerAdapter {

    actual override fun debug(message: String) {
        fireMessage(message, OS_LOG_TYPE_DEBUG)
    }

    actual override fun info(message: String) {
        fireMessage(message, OS_LOG_TYPE_INFO)
    }

    actual override fun warn(message: String) {
        fireMessage(message, OS_LOG_TYPE_ERROR)
    }

    actual override fun error(message: String) {
        fireMessage(message, OS_LOG_TYPE_ERROR)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun fireMessage(message: String, logLevel: os_log_type_t) {
        _os_log_internal(
            __dso_handle.ptr,
            OS_LOG_DEFAULT,
            logLevel,
            message
        )
    }

}