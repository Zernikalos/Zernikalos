/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos

import org.w3c.dom.HTMLCanvasElement
import zernikalos.context.createDefaultContextCreator
import zernikalos.ui.ZJsSurfaceView
import zernikalos.ui.ZSurfaceView

@JsExport
class Zernikalos: ZernikalosBase() {

    @JsName("initializeWithCanvas")
    fun initialize(canvas: HTMLCanvasElement, stateHandler: ZSceneStateHandler) {
        val surfaceView = ZJsSurfaceView(canvas)
        initialize(surfaceView, stateHandler)
    }

    @JsName("initializeWithDefaults")
    fun initialize(view: ZSurfaceView, stateHandler: ZSceneStateHandler) {
        val contextCreator = createDefaultContextCreator()

        initialize(view, contextCreator, stateHandler)
    }

}
