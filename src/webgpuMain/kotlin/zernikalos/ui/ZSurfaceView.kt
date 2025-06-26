/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement

@JsExport
@ExperimentalJsExport
class ZJsSurfaceView(val canvas: HTMLCanvasElement): ZSurfaceView {

    var _eventHandler: ZSurfaceViewEventHandler? = null
    override var eventHandler: ZSurfaceViewEventHandler?
        get() = _eventHandler
        set(value) {
            _eventHandler = value
            onReady()
        }

    override val surfaceWidth: Int
        get() = canvas.width
    override val surfaceHeight: Int
        get() = canvas.height

    init {
        canvas.onresize = { _ ->
            eventHandler?.onResize(surfaceWidth, surfaceHeight)
        }
    }

    private fun onReady() {
        eventHandler?.onReady()
        eventHandler?.onResize(surfaceWidth, surfaceHeight)
        renderLoop()
    }

    private fun renderLoop() {
        window.setInterval({eventHandler?.onRender()}, 1000/60)
    }

}
