/*
 * Copyright (c) 2024-2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.components.shader.internal.ZUniformBlockRenderer
import zernikalos.components.shader.internal.ZUniformInternalRenderer
import zernikalos.components.shader.internal.ZUniformSingleRenderer
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext

actual class ZUniformRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZUniformBlockData
): ZComponentRenderer(ctx) {

    private val internalRenderer: ZUniformInternalRenderer

    init {
        internalRenderer = if (data.uniforms.size == 1) {
            ZUniformSingleRenderer(ctx, data)
        } else {
            ZUniformBlockRenderer(ctx, data)
        }
    }

    actual override fun initialize() = internalRenderer.initialize()

    override fun bind() = internalRenderer.bind()

    override fun unbind() = internalRenderer.unbind()

    fun bindLocation(programId: GLWrap) {
        ctx as ZGLRenderingContext
        when (internalRenderer) {
            is ZUniformSingleRenderer -> {
                val singleData = data.uniforms.values.first()
                internalRenderer.setUniformId(ctx.getUniformLocation(programId, singleData.uniformName))
            }
            is ZUniformBlockRenderer -> internalRenderer.bindLocation(programId)
        }
    }

    actual override fun dispose() {
        internalRenderer.dispose()
    }
}
