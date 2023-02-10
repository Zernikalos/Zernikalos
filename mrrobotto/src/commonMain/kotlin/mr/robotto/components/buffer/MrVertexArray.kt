package mr.robotto.components.buffer

import mr.robotto.GLWrap
import mr.robotto.MrRenderingContext
import mr.robotto.components.MrComponent

class MrVertexArray: MrComponent() {
    private lateinit var vao: GLWrap

    override fun initialize(ctx: MrRenderingContext) {
        val auxVao = ctx.createVertexArray()
        // TODO Check existence
        vao = auxVao
        ctx.bindVertexArray(vao)
    }

    override fun render(ctx: MrRenderingContext) {
        ctx.bindVertexArray(vao)
    }
}
