package mr.robotto.components.buffer

import mr.robotto.GLWrap
import mr.robotto.components.MrComponent

class MrVertexArray: MrComponent() {
    private lateinit var vao: GLWrap

    override fun renderInitialize() {
        val auxVao = context.createVertexArray()
        // TODO Check existence
        vao = auxVao
        context.bindVertexArray(vao)
    }

    override fun render() {
        context.bindVertexArray(vao)
    }
}
