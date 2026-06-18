/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.renderer

import zernikalos.context.ZContext
import zernikalos.context.gpu.ZGpuFrame

actual class ZRenderer actual constructor(ctx: ZContext) : ZRendererBase(ctx) {
    actual fun bind() {
    }

    actual fun unbind() {
    }

    actual fun render() {
        renderFrame()
    }

    actual override fun onViewportResize(width: Int, height: Int) {
        ctx.scene?.onViewportResize(ctx, width, height)
    }

    actual fun dispose() {
    }

    override fun createGpuFrame(): ZGpuFrame? = ZGpuFrame(ctx)
}
