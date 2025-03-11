/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos

import android.opengl.GLSurfaceView
import zernikalos.context.createDefaultContextCreator
import zernikalos.ui.ZAndroidSurfaceView
import zernikalos.ui.ZSurfaceView
import zernikalos.ui.ZernikalosView

class Zernikalos: ZernikalosBase() {

    fun initializeWithNativeView(view: GLSurfaceView, stateHandler: ZSceneStateHandler) {
        val surfaceView = ZAndroidSurfaceView(view)
        setupContext(surfaceView, stateHandler)
    }

    fun initialize(view: ZernikalosView, stateHandler: ZSceneStateHandler) {
        setupContext(view as ZSurfaceView, stateHandler)
    }

    private fun setupContext(view: ZSurfaceView, stateHandler: ZSceneStateHandler) {
        val contextCreator = createDefaultContextCreator()

        initialize(view, contextCreator, stateHandler)
    }

}
