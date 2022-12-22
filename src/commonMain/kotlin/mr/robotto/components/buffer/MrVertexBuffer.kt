package mr.robotto.components.buffer

import mr.robotto.BufferTargetType
import mr.robotto.BufferUsageType
import mr.robotto.GLWrap
import mr.robotto.components.MrComponent

class MrVertexBuffer: MrComponent() {
    lateinit var buffer: GLWrap
    var targetType: BufferTargetType = BufferTargetType.ARRAY_BUFFER
    var usageType: BufferUsageType = BufferUsageType.STATIC_DRAW
    lateinit var dataArray: ByteArray

    override fun renderInitialize() {
        buffer = context.createBuffer()
        // TODO Check errors
        //        if (!data.buffer) {
        //            throw Error("Unable to create buffer")
        //        }

        context.bindBuffer(targetType, buffer)
        context.bufferData(targetType, dataArray, usageType)
        // context.vertexAttribPointer()
        //context.bindBuffer(data.targetType, GLWrap(0))
    }

    override fun render() {
        TODO("Not yet implemented")
    }

}