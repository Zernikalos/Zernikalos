/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGSize
import platform.MetalKit.MTKView
import platform.MetalKit.MTKViewDelegateProtocol
import platform.darwin.NSObject
import platform.darwin.dispatch_semaphore_create
import platform.darwin.dispatch_semaphore_t

class ZMtkViewDelegate() : NSObject(), MTKViewDelegateProtocol {

    private val inFlightSemaphore: dispatch_semaphore_t = dispatch_semaphore_create(3)
    var eventHandler: ZSurfaceViewEventHandler? = null

    init {
        eventHandler?.onReady()
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun mtkView(view: MTKView, drawableSizeWillChange: CValue<CGSize>) {
        drawableSizeWillChange.useContents {
            eventHandler?.onResize(this.width.toInt(), this.height.toInt())
        }
    }

    override fun drawInMTKView(view: MTKView) {
        eventHandler?.onRender()
    }

}