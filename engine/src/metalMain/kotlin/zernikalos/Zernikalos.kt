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
import zernikalos.scenestatehandler.ZSceneStateHandler
import zernikalos.ui.ZMtlSurfaceView
import zernikalos.ui.ZSurfaceView
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("Zernikalos")
class Zernikalos: ZernikalosBase() {

    fun initialize(view: MTKView, stateHandler: ZSceneStateHandler) {
        val surfaceView = ZMtlSurfaceView(view)
        initializeWithDefaults(surfaceView, stateHandler)
    }

    fun initializeWithDefaults(view: ZSurfaceView, stateHandler: ZSceneStateHandler) {
        val contextCreator = createDefaultContextCreator()
        internalInitialize(view, contextCreator, stateHandler)
    }

}