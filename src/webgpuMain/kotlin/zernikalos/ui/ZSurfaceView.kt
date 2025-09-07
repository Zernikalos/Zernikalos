/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

import kotlinx.browser.window
import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement

external class ResizeObserver(callback: (Array<dynamic>) -> Unit) {
    fun observe(target: Element)
}

external class ResizeObserverEntry {
    val target: Element
    val contentRect: DOMRectReadOnly
}

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

    private var pendingResize = false
    private val resizeObserver = ResizeObserver { entries ->
        handleResize(entries)
    }

    init {
        resizeObserver.observe(canvas)
    }

    private fun handleResize(entries: Array<ResizeObserverEntry>) {
        for (entry in entries) {
            if (entry.target == canvas && !pendingResize) {
                pendingResize = true
                window.requestAnimationFrame {
                    try {
                        val contentRect = entry.contentRect
                        val dpr = window.devicePixelRatio

                        val width: Int = (contentRect.width as Double * dpr).toInt()
                        val height: Int = (contentRect.height as Double * dpr).toInt()

                        canvas.width = width
                        canvas.height = height

                        eventHandler?.onResize(width, height)
                    } finally {
                        pendingResize = false
                    }
                }
            }
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
