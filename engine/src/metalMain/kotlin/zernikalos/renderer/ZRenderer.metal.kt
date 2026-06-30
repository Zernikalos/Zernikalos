/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.renderer

import platform.Metal.*
import platform.MetalKit.MTKView
import zernikalos.context.ZContext
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZCullMode
import zernikalos.context.ZFrontFace
import zernikalos.context.gpu.ZGpuFrame
import zernikalos.context.gpu.ZGpuPassState

actual class ZRenderer actual constructor(ctx: ZContext) : ZRendererBase(ctx) {

    private val renderingContext: ZMtlRenderingContext = ctx.renderingContext as ZMtlRenderingContext
    private val nativeView: MTKView = renderingContext.surfaceView.nativeView
    private val depthState: MTLDepthStencilStateProtocol?
    private val gpuFrame: ZGpuFrame

    init {
        val depthDescriptor = MTLDepthStencilDescriptor()
        depthDescriptor.depthCompareFunction = MTLCompareFunctionLess
        depthDescriptor.depthWriteEnabled = true
        depthState = renderingContext.device.newDepthStencilStateWithDescriptor(depthDescriptor)

        nativeView.depthStencilPixelFormat = MTLPixelFormatDepth32Float_Stencil8
        nativeView.colorPixelFormat = MTLPixelFormatBGRA8Unorm
        nativeView.sampleCount = 1u

        gpuFrame = ZGpuFrame(ctx, renderingContext, nativeView, depthState)
    }

    actual fun bind() {
    }

    actual fun unbind() {
    }

    actual fun render() {
        renderFrame()
    }

    override fun createGpuFrame(): ZGpuFrame? = gpuFrame

    override fun configureRenderState() {
        val pass = ctx.renderingContext.activePass ?: return

        pass.applyPassState(
            ZGpuPassState(
                cullMode = ZCullMode.Front,
                frontFace = ZFrontFace.CW,
            )
        )
    }

    actual override fun onViewportResize(width: Int, height: Int) {
        ctx.scene?.onViewportResize(ctx, width, height)
    }

    actual fun dispose() {
    }
}
