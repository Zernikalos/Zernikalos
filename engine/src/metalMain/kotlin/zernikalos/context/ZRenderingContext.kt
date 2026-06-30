/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context

import platform.Metal.*
import zernikalos.context.gpu.ZGpuRenderPass
import zernikalos.ui.ZMtlSurfaceView
import zernikalos.ui.ZSurfaceView

class ZMtlRenderingContext(view: ZSurfaceView): ZRenderingContext {

    val surfaceView: ZMtlSurfaceView = view as ZMtlSurfaceView

    val device: MTLDeviceProtocol
    val commandQueue: MTLCommandQueueProtocol

    var commandBuffer: MTLCommandBufferProtocol? = null
    private var nativeRenderEncoder: MTLRenderCommandEncoderProtocol? = null

    override var activePass: ZGpuRenderPass? = null

    init {
        device = surfaceView.nativeView.device!!
        commandQueue = device.newCommandQueue()!!    }

    override fun initWithSurfaceView(surfaceView: ZSurfaceView) {

    }

    override fun <R> withActivePass(pass: ZGpuRenderPass, block: () -> R): R {
        val previous = activePass
        activePass = pass
        try {
            return block()
        } finally {
            activePass = previous
        }
    }

    internal fun makeCommandBuffer() {
        commandBuffer = commandQueue.commandBuffer()
    }

    internal fun makeRenderCommandEncoder(renderPassDescriptor: MTLRenderPassDescriptor): MTLRenderCommandEncoderProtocol? {
        nativeRenderEncoder = commandBuffer?.renderCommandEncoderWithDescriptor(renderPassDescriptor)
        return nativeRenderEncoder
    }

    internal fun clearActivePass() {
        activePass = null
        nativeRenderEncoder = null
    }

}
