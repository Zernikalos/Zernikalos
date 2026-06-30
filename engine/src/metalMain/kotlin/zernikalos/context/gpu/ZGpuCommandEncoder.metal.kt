/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context.gpu

import platform.Metal.MTLDepthStencilStateProtocol
import platform.Metal.MTLRenderCommandEncoderProtocol
import platform.MetalKit.MTKView
import zernikalos.context.ZGpuRenderPassDescriptor
import zernikalos.context.ZMtlRenderingContext

actual class ZGpuCommandEncoder internal constructor(
    private val renderingContext: ZMtlRenderingContext,
    private val nativeView: MTKView,
    private val depthState: MTLDepthStencilStateProtocol?,
) {
    actual fun beginRenderPass(descriptor: ZGpuRenderPassDescriptor): ZGpuRenderPass? {
        val platformDesc = nativeView.currentRenderPassDescriptor ?: return null
        applyPassDescriptorToMetal(descriptor, platformDesc)

        val encoder = renderingContext.makeRenderCommandEncoder(platformDesc) ?: return null
        encoder.label = "Zernikalos Render Encoder"
        encoder.pushDebugGroup("Zernikalos Draw")

        return ZGpuRenderPass(encoder, depthState, renderingContext)
    }

    actual fun finish() {
        // Metal submits via command buffer in ZGpuFrame.submit().
    }
}
