/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components

import zernikalos.context.ZGpuRenderPassDescriptor
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.gpu.encodeWebGPURenderPassDescriptor
import zernikalos.context.webgpu.*

actual class ZViewportRenderer actual constructor(ctx: ZRenderingContext, private val data: ZViewportData): ZComponentRenderer(ctx) {

    var depthTexture: GPUTexture? = null

    private var cachedPassDescriptor: ZGpuRenderPassDescriptor? = null
    private var colorAttachment: GPURenderPassColorAttachment? = null
    private var depthAttachment: GPURenderPassDepthStencilAttachment? = null
    private var lastSwapchainTexture: GPUTexture? = null
    private var lastBuiltWidth: Int = -1
    private var lastBuiltHeight: Int = -1
    private var depthTexWidth: Int = -1
    private var depthTexHeight: Int = -1

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext
        createDepthTexture()
    }

    private fun createDepthTexture(width: Int = data.viewBox.width, height: Int = data.viewBox.height) {
        ctx as ZWebGPURenderingContext

        val depthWidth = if (width <= 0) 100 else width
        val depthHeight = if (height <= 0) 100 else height

        depthTexture?.destroy()
        depthTexture = ctx.device.createTexture(
            GPUTextureDescriptor(
                size = GPUExtent3D(
                    width = depthWidth,
                    height = depthHeight,
                ),
                format = GPUTextureFormat.Depth24Plus,
                usage = GPUTextureUsage.RENDER_ATTACHMENT
            ).toGpu()
        )
        depthTexWidth = depthWidth
        depthTexHeight = depthHeight
        invalidateDescriptorCache()
    }

    private fun invalidateDescriptorCache() {
        lastSwapchainTexture = null
        lastBuiltWidth = -1
        lastBuiltHeight = -1
        colorAttachment = null
        depthAttachment = null
        cachedPassDescriptor = null
    }

    actual fun buildRenderPassDescriptor(): ZGpuRenderPassDescriptor? {
        ctx as ZWebGPURenderingContext

        if (data.viewBox.width <= 0 || data.viewBox.height <= 0) {
            cachedPassDescriptor = null
            return null
        }

        val swapchainTexture = ctx.webGPUContext?.getCurrentTexture() ?: run {
            cachedPassDescriptor = null
            return null
        }

        if (swapchainTexture.width != depthTexWidth || swapchainTexture.height != depthTexHeight) {
            createDepthTexture(swapchainTexture.width, swapchainTexture.height)
        }

        val depthView = depthTexture?.createView() ?: run {
            cachedPassDescriptor = null
            return null
        }

        val passDescriptor = buildSwapchainPassDescriptor(data.clearColor)
        val needsFullRebuild = cachedPassDescriptor == null
            || lastSwapchainTexture !== swapchainTexture
            || lastBuiltWidth != swapchainTexture.width
            || lastBuiltHeight != swapchainTexture.height
            || colorAttachment == null
            || depthAttachment == null

        if (needsFullRebuild) {
            val textureView = swapchainTexture.createView()
            val clearColor = passDescriptor.colorAttachments.first().clearValue
            colorAttachment = GPURenderPassColorAttachment(
                view = textureView,
                loadOp = GPULoadOp.CLEAR,
                storeOp = GPUStoreOp.STORE,
                clearValue = GPUColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
            )
            // far plane = 1.0; +Z forward convention with LESS depth test (see gpu-rendering-pipeline-refactor §9.4)
            depthAttachment = GPURenderPassDepthStencilAttachment(
                view = depthView,
                depthLoadOp = GPULoadOp.CLEAR,
                depthStoreOp = GPUStoreOp.STORE,
                depthClearValue = 1.0f
            )
            lastSwapchainTexture = swapchainTexture
            lastBuiltWidth = swapchainTexture.width
            lastBuiltHeight = swapchainTexture.height
        } else {
            val clearColor = passDescriptor.colorAttachments.first().clearValue
            colorAttachment!!.view = swapchainTexture.createView()
            colorAttachment!!.clearValue = GPUColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
            depthAttachment!!.view = depthView
        }

        cachedPassDescriptor = passDescriptor
        return cachedPassDescriptor
    }

    internal fun encodeNativeRenderPass(desc: ZGpuRenderPassDescriptor): GPURenderPassDescriptor? {
        val color = colorAttachment ?: return null
        val depth = depthAttachment ?: return null
        return encodeWebGPURenderPassDescriptor(desc, color, depth)
    }

    actual override fun render() {
        // No-op on WebGPU: descriptor is built in renderViewports via buildRenderPassDescriptor().
        // ZScene still calls viewport.render() for API parity with OpenGL; clear is via pass loadOp.
    }

    actual fun onViewportResize(width: Int, height: Int) {
        ctx as ZWebGPURenderingContext
        createDepthTexture(width, height)
    }

    override fun dispose() {
        depthTexture?.destroy()
        depthTexture = null
        depthTexWidth = -1
        depthTexHeight = -1
        invalidateDescriptorCache()
    }
}
