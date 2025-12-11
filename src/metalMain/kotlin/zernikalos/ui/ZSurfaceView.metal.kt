/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.MetalKit.MTKView
import zernikalos.events.ZEventQueue

@OptIn(ExperimentalForeignApi::class)
class ZMtlSurfaceView(view: MTKView): ZSurfaceView {

    var nativeView: MTKView
    private val viewDelegate: ZMtkViewDelegate = ZMtkViewDelegate()
    private val touchEventConverter = MetalTouchEventConverter()
    private val touchHandler: MetalTouchHandler = MetalTouchHandler(view, touchEventConverter)

    override val surfaceWidth: Int
        get() {
            nativeView.drawableSize().useContents {
                val widthValue = this.width
                return widthValue.toInt()
            }
        }

    override val surfaceHeight: Int
        get() {
            nativeView.drawableSize.useContents {
                val heightValue = this.height
                return heightValue.toInt()
            }
        }

    override var eventHandler: ZSurfaceViewEventHandler?
        get() = viewDelegate.eventHandler
        set(value) {
            viewDelegate.eventHandler = value
            value?.onResize(surfaceWidth, surfaceHeight)
        }

    override var eventQueue: ZEventQueue?
        get() = touchHandler.eventQueue
        set(value) {
            touchHandler.setEventQueue(value)
            // If the view is a ZMtkViewWithTouch, set up the touch handler
            setupTouchHandlerIfNeeded()
        }

    init {
        nativeView = view
        setViewDelegate()
        setupTouchHandlerIfNeeded()
    }

    private fun setupTouchHandlerIfNeeded() {
        // Check if the view is a ZMtkViewWithTouch and set up touch handling
        // If the view is not a ZMtkViewWithTouch, touch events will not be available
        val viewWithTouch = nativeView as? ZMtkViewWithTouch
        viewWithTouch?.touchHandler = touchHandler
    }

    private fun setViewDelegate() {
        viewDelegate.mtkView(nativeView, nativeView.drawableSize)
        nativeView.delegate = viewDelegate
    }

    override fun dispose() {
        touchHandler.dispose()
        viewDelegate.dispose()
    }

}
