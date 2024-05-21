/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos

import platform.MetalKit.MTKView
import zernikalos.context.createDefaultContextCreator
import zernikalos.ui.ZSurfaceView

class Zernikalos: ZernikalosBase() {

    fun initialize(view: MTKView, stateHandler: ZSceneStateHandler) {
        val surfaceView = ZSurfaceView(view)
        initializeWithDefaults(surfaceView, stateHandler)
    }

    fun initializeWithDefaults(view: ZSurfaceView, stateHandler: ZSceneStateHandler) {
        val contextCreator = createDefaultContextCreator()

        initialize(view, contextCreator, stateHandler)
    }

}