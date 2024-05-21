/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos

import zernikalos.context.ZRenderingContext
import zernikalos.context.ZSceneContext
import kotlin.js.JsExport

@JsExport
interface ZSceneStateHandler {

    fun onReady(sceneContext: ZSceneContext, renderingContext: ZRenderingContext)

    fun onResize(width: Int, height: Int)

    fun onRender(sceneContext: ZSceneContext, renderingContext: ZRenderingContext)
}