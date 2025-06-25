/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components

import zernikalos.context.*
import zernikalos.context.webgpu.GPUColor
import zernikalos.context.webgpu.GPUExtent3D
import zernikalos.context.webgpu.GPURenderPassColorAttachment
import zernikalos.context.webgpu.GPURenderPassDepthStencilAttachment
import zernikalos.context.webgpu.GPURenderPassDescriptor
import zernikalos.context.webgpu.GPUTexture
import zernikalos.context.webgpu.GPUTextureDescriptor
import zernikalos.context.webgpu.GPUTextureFormat
import zernikalos.context.webgpu.GPUTextureUsage

actual class ZViewportRenderer actual constructor(ctx: ZRenderingContext, private val data: ZViewportData): ZComponentRenderer(ctx) {

    var depthTexture: GPUTexture? = null
    var renderPassDescriptor: GPURenderPassDescriptor? = null

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        // Depth texture creation
        createDepthTexture()
    }

    private fun createDepthTexture() {
        ctx as ZWebGPURenderingContext

        // TODO: weird hack to avoid initial failures
        var width = data.viewBox.width
        var height = data.viewBox.height
        if (width == 0) {
            width = 100
        }
        if (height == 0) {
            height = 100
        }

        depthTexture = ctx.device.createTexture(
            GPUTextureDescriptor(
                size = GPUExtent3D(
                    width = width,
                    height = height,
                ),
                format = GPUTextureFormat.Depth24Plus,
                usage = GPUTextureUsage.RENDER_ATTACHMENT
            ).toGpu()
        )
    }

    private fun createRenderPassDescriptor() {
        ctx as ZWebGPURenderingContext

        val textureView = ctx.webGPUContext?.getCurrentTexture()?.createView()
        val depthView = depthTexture?.createView()

        val clearColor = data.clearColor
        val colorAttachment = GPURenderPassColorAttachment(
            view = textureView!!,
            loadOp = "clear",
            storeOp = "store",
            clearValue = GPUColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
        )

        val depthAttachment = GPURenderPassDepthStencilAttachment(
            view = depthView!!,
            depthLoadOp = "clear",
            depthStoreOp = "store",
            depthClearValue = 1.0f
        )

        renderPassDescriptor = GPURenderPassDescriptor(
            colorAttachments = arrayOf(colorAttachment),
            depthStencilAttachment = depthAttachment
        )
    }

    actual override fun render() {
        ctx as ZWebGPURenderingContext
        createRenderPassDescriptor()
    }

    actual fun onViewportResize(width: Int, height: Int) {
        ctx as ZWebGPURenderingContext
        createDepthTexture()
    }
}
