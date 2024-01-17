package zernikalos.logger

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ptr
import platform.darwin.OS_LOG_DEFAULT
import platform.darwin.OS_LOG_TYPE_DEBUG
import platform.darwin.__dso_handle
import platform.darwin._os_log_internal

actual class ZLoggerAdapterConsole : ZLoggerAdapter() {
    @OptIn(ExperimentalForeignApi::class)
    override fun debug(message: String) {
        _os_log_internal(
            __dso_handle.ptr,
            OS_LOG_DEFAULT,
            OS_LOG_TYPE_DEBUG,
            message
        )
    }

}