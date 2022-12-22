package mr.robotto.components.buffer

import mr.robotto.GLWrap
import mr.robotto.components.MrComponent
import mr.robotto.components.MrComponentData
import mr.robotto.components.MrComponentRender

class MrVertexArray: MrComponent<MrVertexArrayData, MrVertexArrayRender>() {
    override var data: MrVertexArrayData = MrVertexArrayData()
    override var renderer: MrVertexArrayRender = MrVertexArrayRender()
}

class MrVertexArrayData: MrComponentData() {
    lateinit var vao: GLWrap
}

class MrVertexArrayRender: MrComponentRender<MrVertexArrayData>() {
    override fun internalInitialize() {
        val vao = context.createVertexArray()
        // TODO Check existence
        data.vao = vao
        context.bindVertexArray(data.vao)
    }

    override fun render() {
        context.bindVertexArray(data.vao)
    }

}