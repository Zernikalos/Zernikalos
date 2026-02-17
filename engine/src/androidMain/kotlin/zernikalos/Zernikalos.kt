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
import zernikalos.scenestatehandler.ZSceneStateHandler
import zernikalos.ui.ZAndroidSurfaceView
import zernikalos.ui.ZernikalosView

class Zernikalos: ZernikalosBase() {

    fun initialize(view: GLSurfaceView, stateHandler: ZSceneStateHandler) {
        val surfaceView = ZAndroidSurfaceView(view)
        val contextCreator = createDefaultContextCreator()
        internalInitialize(surfaceView, contextCreator, stateHandler)
    }

    fun initialize(view: ZernikalosView, stateHandler: ZSceneStateHandler) {
        val contextCreator = createDefaultContextCreator()
        internalInitialize(view, contextCreator, stateHandler)
    }

}
