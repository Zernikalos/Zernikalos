/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import zernikalos.components.ZBindeable
import zernikalos.components.ZComponentRenderer
import zernikalos.components.ZRenderizableComponent
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger

class ZVertexArray(): ZRenderizableComponent<ZVertexArrayRenderer>(), ZBindeable {

    override fun createRenderer(ctx: ZRenderingContext): ZVertexArrayRenderer {
        return ZVertexArrayRenderer(ctx)
    }

    override fun bind() {
        renderer.bind()
    }

    override fun unbind() {
        renderer.unbind()
    }

}

class ZVertexArrayRenderer(ctx: ZRenderingContext): ZComponentRenderer(ctx) {

    private lateinit var vao: GLWrap

    override fun initialize() {
        ctx as ZGLRenderingContext

        val auxVao = ctx.createVertexArray()
        // TODO Check existence
        vao = auxVao
        ctx.bindVertexArray(vao)
        logger.debug("Creating and binding VAO with id ${vao.id}")
    }

    override fun bind() {
        ctx as ZGLRenderingContext

        logger.debugOnce("Binding VAO with id ${vao.id}")
        ctx.bindVertexArray(vao)
    }

    override fun unbind() {
        ctx as ZGLRenderingContext

        ctx.bindVertexArray(null)
    }

}
