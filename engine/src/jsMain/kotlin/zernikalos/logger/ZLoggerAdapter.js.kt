/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.logger

import kotlin.js.Console

abstract external class WindowConsole: Console {
    fun debug(vararg o: Any?)
}

external val console: WindowConsole

actual class ZLoggerAdapterConsole: ZLoggerAdapter {
    actual override fun debug(message: String) {
        console.debug(message)
    }

    actual override fun info(message: String) {
        console.info(message)
    }

    actual override fun warn(message: String) {
        console.warn(message)
    }

    actual override fun error(message: String) {
        console.error(message)
    }

}