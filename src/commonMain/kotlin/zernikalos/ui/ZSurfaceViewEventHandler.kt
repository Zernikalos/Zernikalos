/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

import kotlin.js.JsExport

@JsExport
interface ZSurfaceViewEventHandler {

    fun onReady()

    fun onRender()

    fun onResize(width: Int, height: Int)
}