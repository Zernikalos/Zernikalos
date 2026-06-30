/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context.gpu

import platform.Metal.MTLDepthStencilStateProtocol
import platform.MetalKit.MTKView
import zernikalos.context.ZContext
import zernikalos.context.ZMtlRenderingContext

actual class ZGpuFrame internal constructor(
    private val ctx: ZContext,
    private val renderingContext: ZMtlRenderingContext,
    private val nativeView: MTKView,
    private val depthState: MTLDepthStencilStateProtocol?,
) {
    actual fun begin(): Boolean {
        renderingContext.makeCommandBuffer()
        return renderingContext.commandBuffer != null
    }

    actual fun beginRecording(): ZGpuCommandEncoder? {
        if (renderingContext.commandBuffer == null) {
            return null
        }
        return ZGpuCommandEncoder(renderingContext, nativeView, depthState)
    }

    actual fun submit(encoder: ZGpuCommandEncoder) {
        val drawable = nativeView.currentDrawable ?: return
        renderingContext.commandBuffer?.presentDrawable(drawable)
        renderingContext.commandBuffer?.commit()
    }

    actual fun end() {
        renderingContext.commandBuffer = null
    }
}
