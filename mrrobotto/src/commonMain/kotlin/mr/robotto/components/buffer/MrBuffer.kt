package mr.robotto.components.buffer

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.cbor.ByteString
import mr.robotto.BufferTargetType
import mr.robotto.BufferUsageType
import mr.robotto.GLWrap
import mr.robotto.components.MrComponent

@Serializable
class MrBuffer: MrComponent() {

    @Transient
    lateinit var buffer: GLWrap

    @ByteString
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
    }

    override fun render() {
    }
}
