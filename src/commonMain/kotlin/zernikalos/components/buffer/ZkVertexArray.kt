package zernikalos.components.buffer

import zernikalos.GLWrap
import zernikalos.ZkRenderingContext
import zernikalos.components.ZkComponent

class ZkVertexArray: ZkComponent() {
    private lateinit var vao: GLWrap

    override fun initialize(ctx: ZkRenderingContext) {
        val auxVao = ctx.createVertexArray()
        // TODO Check existence
        vao = auxVao
        ctx.bindVertexArray(vao)
    }

    override fun render(ctx: ZkRenderingContext) {
        ctx.bindVertexArray(vao)
    }
}
