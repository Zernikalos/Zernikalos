/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context.gpu

import zernikalos.context.ZContext
import zernikalos.context.ZVulkanRenderingContext

actual class ZGpuFrame internal constructor(private val ctx: ZContext) {
    private val gpuCtx: ZVulkanRenderingContext = ctx.renderingContext as ZVulkanRenderingContext

    actual fun begin(): Boolean = gpuCtx.nativeHandle != 0L

    actual fun beginRecording(): ZGpuCommandEncoder? {
        if (gpuCtx.nativeHandle == 0L) return null
        return ZGpuCommandEncoder(ctx, gpuCtx)
    }

    actual fun submit(encoder: ZGpuCommandEncoder) {
        gpuCtx.renderNativeFrame()
    }

    actual fun end() {
    }
}
