package zernikalos.components.buffer

import zernikalos.GLWrap
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZVertexArrayRenderer: ZComponentRender<ZVertexArrayData> {

    private lateinit var vao: GLWrap

    actual override fun initialize(ctx: ZRenderingContext, data: ZVertexArrayData) {
        val auxVao = ctx.createVertexArray()
        // TODO Check existence
        vao = auxVao
        ctx.bindVertexArray(vao)
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZVertexArrayData) {
        ctx.bindVertexArray(vao)
    }

    actual override fun unbind(ctx: ZRenderingContext, data: ZVertexArrayData) {
    }

}