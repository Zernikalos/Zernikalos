package zernikalos.components.buffer

import zernikalos.GLWrap
import zernikalos.ZGLRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZVertexArrayRenderer: ZComponentRender<ZVertexArrayData> {

    private lateinit var vao: GLWrap

    actual override fun initialize(ctx: ZRenderingContext, data: ZVertexArrayData) {
        ctx as ZGLRenderingContext

        val auxVao = ctx.createVertexArray()
        // TODO Check existence
        vao = auxVao
        ctx.bindVertexArray(vao)
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZVertexArrayData) {
        ctx as ZGLRenderingContext

        ctx.bindVertexArray(vao)
    }

    actual override fun unbind(ctx: ZRenderingContext, data: ZVertexArrayData) {
    }

}