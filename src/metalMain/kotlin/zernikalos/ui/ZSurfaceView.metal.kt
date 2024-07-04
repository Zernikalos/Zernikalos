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

actual class ZSurfaceView(view: MTKView) {

    var nativeView: MTKView
    private val viewDelegate: ZMtkViewDelegate = ZMtkViewDelegate()

    @OptIn(ExperimentalForeignApi::class)
    actual val width: Int
        get() {
            nativeView.drawableSize().useContents {
                val widthValue = this.width
                return widthValue.toInt()
            }
        }
    @OptIn(ExperimentalForeignApi::class)
    actual val height: Int
        get() {
            nativeView.drawableSize.useContents {
                val heightValue = this.height
                return heightValue.toInt()
            }
        }

    actual var eventHandler: ZSurfaceViewEventHandler?
        get() = viewDelegate.eventHandler
        set(value) {
            viewDelegate.eventHandler = value
        }

    init {
        nativeView = view
        setViewDelegate()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun setViewDelegate() {
        viewDelegate.mtkView(nativeView, nativeView.drawableSize)
        nativeView.delegate = viewDelegate
    }

}