/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context

import platform.Metal.*
import zernikalos.ui.ZMtlSurfaceView
import zernikalos.ui.ZSurfaceView

class ZMtlRenderingContext(view: ZSurfaceView): ZRenderingContext {

    val surfaceView: ZMtlSurfaceView = view as ZMtlSurfaceView

    val device: MTLDeviceProtocol
    val commandQueue: MTLCommandQueueProtocol

    var commandBuffer: MTLCommandBufferProtocol? = null
    var renderEncoder: MTLRenderCommandEncoderProtocol? = null

    init {
        device = surfaceView.nativeView.device!!
        commandQueue = device.newCommandQueue()!!    }

    override fun initWithSurfaceView(surfaceView: ZSurfaceView) {

    }

    fun makeCommandBuffer() {
        commandBuffer = commandQueue.commandBuffer()
    }

    fun makeRenderCommandEncoder(renderPassDescriptor: MTLRenderPassDescriptor) {
        renderEncoder = commandBuffer?.renderCommandEncoderWithDescriptor(renderPassDescriptor)
    }

}
