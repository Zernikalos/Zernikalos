package mr.robotto.components.buffer

import mr.robotto.BufferTargetType
import mr.robotto.BufferUsageType
import mr.robotto.GLWrap
import mr.robotto.components.MrComponent
import mr.robotto.components.MrComponentData
import mr.robotto.components.MrComponentRender

class MrVertexBuffer: MrComponent<MrVertexBufferData, MrVertexBufferRender>() {
    override var data: MrVertexBufferData = MrVertexBufferData()
    override var renderer: MrVertexBufferRender = MrVertexBufferRender()
}

class MrVertexBufferData: MrComponentData() {
    lateinit var buffer: GLWrap
    var targetType: BufferTargetType = BufferTargetType.ARRAY_BUFFER
    var usageType: BufferUsageType = BufferUsageType.STATIC_DRAW
    lateinit var dataArray: ByteArray
}

class MrVertexBufferRender: MrComponentRender<MrVertexBufferData>() {
    override fun internalInitialize() {
        data.buffer = context.createBuffer()
        // TODO Check errors
//        if (!data.buffer) {
//            throw Error("Unable to create buffer")
//        }

        context.bindBuffer(data.targetType, data.buffer)
        context.bufferData(data.targetType, data.dataArray, data.usageType)
        // context.vertexAttribPointer()
        //context.bindBuffer(data.targetType, GLWrap(0))
    }

    override fun render() {}

}