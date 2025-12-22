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

    /**
     * Event queue for user input events (touch, etc.).
     * Not yet implemented for Metal/iOS platform.
     */
    override var eventQueue: ZEventQueue? = null

    init {
        nativeView = view
        setViewDelegate()
    }

    private fun setViewDelegate() {
        viewDelegate.mtkView(nativeView, nativeView.drawableSize)
        nativeView.delegate = viewDelegate
    }

    override fun dispose() {
        viewDelegate.dispose()
    }

}
