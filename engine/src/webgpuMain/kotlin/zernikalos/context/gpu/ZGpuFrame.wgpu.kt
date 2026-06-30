/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context.gpu

import zernikalos.context.ZContext
import zernikalos.context.ZWebGPURenderingContext

actual class ZGpuFrame internal constructor(
    private val ctx: ZContext,
) {
    private val gpuCtx: ZWebGPURenderingContext = ctx.renderingContext as ZWebGPURenderingContext

    actual fun begin(): Boolean {
        return gpuCtx.createCommandEncoder() != null
    }

    actual fun beginRecording(): ZGpuCommandEncoder? {
        if (gpuCtx.commandEncoder == null) {
            return null
        }
        return ZGpuCommandEncoder(ctx, gpuCtx)
    }

    actual fun submit(encoder: ZGpuCommandEncoder) {
        val commandBuffer = gpuCtx.commandEncoder?.finish() ?: return
        gpuCtx.queue.submit(arrayOf(commandBuffer))
        gpuCtx.commandEncoder = null
    }

    actual fun end() {
        gpuCtx.commandEncoder = null
    }
}
