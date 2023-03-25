package zernikalos.components.buffer

import zernikalos.GLWrap
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponent

class ZVertexArray: ZComponent() {
    private lateinit var vao: GLWrap

    override fun initialize(ctx: ZRenderingContext) {
        val auxVao = ctx.createVertexArray()
        // TODO Check existence
        vao = auxVao
        ctx.bindVertexArray(vao)
    }

    override fun render(ctx: ZRenderingContext) {
        ctx.bindVertexArray(vao)
    }
}
