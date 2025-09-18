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

/**
 * @suppress
 */
external class ResizeObserver(callback: (Array<dynamic>) -> Unit) {
    fun observe(target: Element)
    fun disconnect()
}

/**
 * @suppress
 */
external class ResizeObserverEntry {
    val target: Element
    val contentRect: DOMRectReadOnly
}

/**
 * JavaScript implementation of ZSurfaceView for WebGPU rendering.
 *
 * This class provides a surface view that automatically handles canvas resizing
 * using ResizeObserver and maintains proper aspect ratio and device pixel ratio.
 *
 * @param canvas The HTML canvas element to render to
 *
 * @see ZSurfaceView
 */
@JsExport
@ExperimentalJsExport
class ZJsSurfaceView(val canvas: HTMLCanvasElement): ZSurfaceView {

    /**
     * Internal event handler storage
     */
    private var _eventHandler: ZSurfaceViewEventHandler? = null

    private var rendererIntervalId: Int? = null

    private var animationFrameRequestId: Int? = null

    /**
     * Event handler for surface view events (ready, resize, render).
     * When set, automatically calls onReady() to initialize the surface.
     */
    override var eventHandler: ZSurfaceViewEventHandler?
        get() = _eventHandler
        set(value) {
            _eventHandler = value
            onReady()
        }

    /**
     * Current surface width in pixels
     */
    override val surfaceWidth: Int
        get() = canvas.width

    /**
     * Current surface height in pixels
     */
    override val surfaceHeight: Int
        get() = canvas.height

    /**
     * Flag to prevent multiple resize events from being processed simultaneously
     */
    private var pendingResize = false

    /**
     * ResizeObserver instance that monitors canvas size changes
     */
    private val resizeObserver = ResizeObserver { entries ->
        handleResize(entries)
    }

    init {
        resizeObserver.observe(canvas)
    }

    override fun dispose() {
        val rendererIntervalId = this.rendererIntervalId
        if (rendererIntervalId != null) {
            window.clearInterval(rendererIntervalId)
        }
        val animationFrameRequestId = this.animationFrameRequestId
        if (animationFrameRequestId != null) {
            window.cancelAnimationFrame(animationFrameRequestId)
        }
        resizeObserver.disconnect()
    }

    /**
     * Handles resize events from ResizeObserver.
     * Updates canvas dimensions with proper device pixel ratio scaling
     * and notifies the event handler.
     *
     * @param entries Array of ResizeObserverEntry objects containing resize information
     */
    private fun handleResize(entries: Array<ResizeObserverEntry>) {
        for (entry in entries) {
            if (entry.target == canvas && !pendingResize) {
                pendingResize = true
                animationFrameRequestId = window.requestAnimationFrame {
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

    /**
     * Initializes the surface view when an event handler is set.
     * Calls onReady() and onResize() on the event handler, then starts the render loop.
     */
    private fun onReady() {
        eventHandler?.onReady()
        eventHandler?.onResize(surfaceWidth, surfaceHeight)
        renderLoop()
    }

    /**
     * Starts the render loop at 60 FPS.
     * Calls the event handler's onRender() method on each frame.
     */
    private fun renderLoop() {
        rendererIntervalId = window.setInterval({eventHandler?.onRender()}, 1000/60)
    }

}
