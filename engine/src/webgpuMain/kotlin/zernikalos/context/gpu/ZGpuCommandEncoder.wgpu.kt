/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context.gpu

import zernikalos.components.ZViewportRenderer
import zernikalos.context.ZContext
import zernikalos.context.ZGpuRenderPassDescriptor
import zernikalos.context.ZWebGPURenderingContext

actual class ZGpuCommandEncoder internal constructor(
    private val ctx: ZContext,
    private val gpuCtx: ZWebGPURenderingContext,
) {
    actual fun beginRenderPass(descriptor: ZGpuRenderPassDescriptor): ZGpuRenderPass? {
        val viewport = ctx.scene?.viewport ?: return null
        val viewportRenderer = viewport.renderer as ZViewportRenderer
        val nativeDesc = viewportRenderer.encodeNativeRenderPass(descriptor) ?: return null
        val nativePass = gpuCtx.beginRenderPass(nativeDesc.toGpu()) ?: return null
        return ZGpuRenderPass(nativePass, gpuCtx)
    }

    actual fun finish() {
        // Native command buffer finish happens in ZGpuFrame.submit().
    }
}
