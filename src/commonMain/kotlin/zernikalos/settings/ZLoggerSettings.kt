/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.settings

import zernikalos.logger.ZLogLevel
import zernikalos.logger.ZLoggerAdapter
import zernikalos.logger.ZLoggerAdapterConsole
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