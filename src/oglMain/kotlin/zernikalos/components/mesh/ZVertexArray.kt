/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import zernikalos.components.ZBindeable2
import zernikalos.components.ZComponent2
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger

class ZVertexArray(): ZComponent2(), ZBindeable2 {

    private lateinit var vao: GLWrap

    override val isRenderizable: Boolean = true

    override fun internalRenderInitialize(ctx: ZRenderingContext) {
        ctx as ZGLRenderingContext

        val auxVao = ctx.createVertexArray()
        // TODO Check existence
        vao = auxVao
        ctx.bindVertexArray(vao)
        logger.debug("Creating and binding VAO with id ${vao.id}")
    }

    override fun bind(ctx: ZRenderingContext) {
        ctx as ZGLRenderingContext

        logger.debugOnce("Binding VAO with id ${vao.id}")
        ctx.bindVertexArray(vao)
    }

    override fun unbind(ctx: ZRenderingContext) {
    }

}
