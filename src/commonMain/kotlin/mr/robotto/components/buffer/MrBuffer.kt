package mr.robotto.components.buffer

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.BufferTargetType
import mr.robotto.BufferUsageType
import mr.robotto.GLWrap
import mr.robotto.components.MrComponent

@Serializable
class MrBuffer: MrComponent() {
    @Transient
    lateinit var buffer: GLWrap

    private lateinit var dataArray: ByteArray
    private var targetBuffer: BufferTargetType = BufferTargetType.ARRAY_BUFFER
    private var usage: BufferUsageType = BufferUsageType.STATIC_DRAW


    override fun renderInitialize() {
        buffer = context.createBuffer()
        // TODO Check errors
        //        if (!data.buffer) {
        //            throw Error("Unable to create buffer")
        //        }

        context.bindBuffer(targetBuffer, buffer)
        context.bufferData(targetBuffer, dataArray, usage)
        //context.enableVertexAttrib(0)
        //context.vertexAttribPointer(0, 3, Types.FLOAT.value, false, 0, 0)
    }

    override fun render() {
    }
}