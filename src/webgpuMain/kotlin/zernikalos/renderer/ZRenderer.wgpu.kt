/*
 *
 *  * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package zernikalos.renderer

import zernikalos.context.ZContext
import zernikalos.context.ZWebGPURenderingContext

actual class ZRenderer actual constructor(ctx: ZContext): ZRendererBase(ctx) {

    override fun initialize() {
        ctx.scene!!.initialize(ctx)
    }

    actual fun bind() {
    }

    actual fun unbind() {
    }

    actual fun render() {
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext

        gpuCtx.createCommandEncoder()
        // TODO: Remove this thing from here
        ctx.scene!!.viewport.render()

        gpuCtx.createRenderPass(ctx.scene!!.viewport.renderer.renderPassDescriptor!!.toGpu())
        ctx.scene!!.render(ctx)
        gpuCtx.renderPass?.end()

        gpuCtx.queue.submit(arrayOf(gpuCtx.commandEncoder!!.finish()))
    }

    actual override fun onViewportResize(width: Int, height: Int) {
        ctx.scene?.viewport?.onViewportResize(width, height)
    }
}
