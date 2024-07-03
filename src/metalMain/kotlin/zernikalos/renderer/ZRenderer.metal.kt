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

actual class ZRenderer actual constructor(ctx: ZContext) : ZRendererBase(ctx) {

    private val renderingContext: ZMtlRenderingContext = ctx.renderingContext as ZMtlRenderingContext
    private val nativeView: MTKView = renderingContext.surfaceView.nativeView
    private val depthState: MTLDepthStencilStateProtocol?

    init {
        val depthDescriptor = MTLDepthStencilDescriptor()
        depthDescriptor.depthCompareFunction = MTLCompareFunctionLess
        depthDescriptor.depthWriteEnabled = true
        depthState = renderingContext.device.newDepthStencilStateWithDescriptor(depthDescriptor)
    }

    override fun bind() {
    }

    override fun unbind() {
    }

    override fun render() {
        /// Per frame updates hare

        renderingContext.makeCommandBuffer()

        if (renderingContext.commandBuffer == null) {
            return
        }

        val renderPassDescriptor = nativeView.currentRenderPassDescriptor ?: return

        renderingContext.makeRenderCommandEncoder(renderPassDescriptor)

        val renderEncoder = renderingContext.renderEncoder ?: return

        renderEncoder.label = "Zernikalos Render Encoder"
        renderEncoder.pushDebugGroup("Zernikalos Draw")

        renderEncoder.setCullMode(MTLCullModeFront)
        renderEncoder.setFrontFacingWinding(MTLWindingClockwise)
        renderEncoder.setDepthStencilState(depthState)

        // TODO: Draw call
        ctx.scene?.render(ctx)

        renderingContext.renderEncoder?.popDebugGroup()
        renderingContext.renderEncoder?.endEncoding()

        val drawable = nativeView.currentDrawable ?: return
        renderingContext.commandBuffer?.presentDrawable(drawable)

        renderingContext.commandBuffer?.commit()
    }
}