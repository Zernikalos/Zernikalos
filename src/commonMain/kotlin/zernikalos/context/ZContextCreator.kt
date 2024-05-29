/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context

import zernikalos.ui.ZSurfaceView
import kotlin.js.JsExport

abstract class ZContextCreator {

    fun createContext(surfaceView: ZSurfaceView): ZContext {
        val sceneContext = createSceneContext(surfaceView)
        val renderingContext = createRenderingContext(surfaceView)
        return ZContext(sceneContext, renderingContext)
    }

    abstract fun createSceneContext(surfaceView: ZSurfaceView): ZSceneContext

    abstract fun createRenderingContext(surfaceView: ZSurfaceView): ZRenderingContext

}