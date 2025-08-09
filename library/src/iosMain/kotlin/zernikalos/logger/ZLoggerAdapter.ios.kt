/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
        fireMessage(message, OS_LOG_TYPE_DEFAULT)
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