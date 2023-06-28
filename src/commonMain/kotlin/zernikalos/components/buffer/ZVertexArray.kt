package zernikalos.components.buffer

import zernikalos.GLWrap
import zernikalos.ZRenderingContext
import zernikalos.components.ZBindeable

class ZVertexArray: ZBindeable() {
    private lateinit var vao: GLWrap

    override fun initialize(ctx: ZRenderingContext) {
        val auxVao = ctx.createVertexArray()
        // TODO Check existence
        vao = auxVao
        ctx.bindVertexArray(vao)
    }

    override fun bind(ctx: ZRenderingContext) {
        ctx.bindVertexArray(vao)
    }

    override fun unbind(ctx: ZRenderingContext) {
    }

}
