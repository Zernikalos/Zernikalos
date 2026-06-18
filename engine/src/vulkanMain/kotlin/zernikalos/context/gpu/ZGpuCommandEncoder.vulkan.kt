/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context.gpu

import zernikalos.context.ZContext
import zernikalos.context.ZGpuRenderPassDescriptor
import zernikalos.context.ZVulkanRenderingContext

actual class ZGpuCommandEncoder internal constructor(
    private val ctx: ZContext,
    private val gpuCtx: ZVulkanRenderingContext,
) {
    actual fun beginRenderPass(descriptor: ZGpuRenderPassDescriptor): ZGpuRenderPass? {
        if (gpuCtx.nativeHandle == 0L) return null
        gpuCtx.configureNativeRenderPass(descriptor)
        return ZGpuRenderPass(gpuCtx)
    }

    actual fun finish() {
    }
}
