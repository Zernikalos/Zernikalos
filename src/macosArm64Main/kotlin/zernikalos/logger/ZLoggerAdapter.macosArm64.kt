package zernikalos.logger

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.printf

actual class ZLoggerAdapterConsole : ZLoggerAdapter {
    @OptIn(ExperimentalForeignApi::class)
    actual override fun debug(message: String) {
//        _os_log_internal(
//            __dso_handle.ptr,
//            OS_LOG_DEFAULT,
//            OS_LOG_TYPE_DEBUG,
//            message
//        )
        // TODO All of these must be changed
        printf("$message\n")
    }

    actual override fun info(message: String) {
        printf("$message\n")
    }

    actual override fun warn(message: String) {
        printf("$message\n")
    }

    actual override fun error(message: String) {
        printf("$message\n")
    }

}