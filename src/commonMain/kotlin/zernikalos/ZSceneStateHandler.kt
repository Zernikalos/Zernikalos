/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos

import zernikalos.context.ZContext
import kotlin.js.JsExport

@JsExport
interface ZSceneStateHandler {

    fun onReady(context: ZContext, done: () -> Unit)

    fun onResize(context: ZContext, width: Int, height: Int, done: () -> Unit)

    fun onRender(context: ZContext, done: () -> Unit)
}