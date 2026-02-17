/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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