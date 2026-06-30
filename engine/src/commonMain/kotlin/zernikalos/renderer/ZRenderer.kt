/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.renderer

import zernikalos.components.ZResizable
import zernikalos.context.ZContext
import zernikalos.context.gpu.ZGpuCommandEncoder
import zernikalos.context.gpu.ZGpuFrame
import zernikalos.logger.ZLoggable

abstract class ZRendererBase(protected val ctx: ZContext): ZLoggable, ZResizable {

    open fun initialize() {
        val scene = ctx.sceneContext.scene
        scene?.initialize(ctx)
    }

    open fun update() {
        // Process all accumulated input events synchronously with the frame
        if (!ctx.eventQueue.isEmpty) {
            ctx.eventQueue.processAll()
        }
    }

    protected open fun createGpuFrame(): ZGpuFrame? = null

    protected fun renderFrame() {
        val gpuFrame = createGpuFrame()
        if (gpuFrame == null) {
            // OpenGL stays on its immediate-mode path until the Phase F pass adapter lands.
            renderScene()
            return
        }

        if (!gpuFrame.begin()) return

        try {
            val encoder = gpuFrame.beginRecording() ?: return
            renderViewports(encoder)
            encoder.finish()
            gpuFrame.submit(encoder)
        } finally {
            gpuFrame.end()
        }
    }

    protected open fun renderViewports(encoder: ZGpuCommandEncoder) {
        val viewport = ctx.scene?.viewport ?: return
        val desc = viewport.buildRenderPassDescriptor() ?: return
        val pass = encoder.beginRenderPass(desc) ?: return

        try {
            ctx.renderingContext.withActivePass(pass) {
                configureRenderState()
                renderScene()
            }
        } finally {
            pass.end()
        }
    }

    protected open fun configureRenderState() {
    }

    protected open fun renderScene() {
        ctx.scene?.render(ctx)
    }

}

expect class ZRenderer(ctx: ZContext): ZRendererBase {
    fun bind()
    fun unbind()
    fun render()
    override fun onViewportResize(width: Int, height: Int)
    fun dispose()
}
