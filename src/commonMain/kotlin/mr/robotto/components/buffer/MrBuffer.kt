package mr.robotto.components.buffer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.cbor.ByteString
import mr.robotto.BufferTargetType
import mr.robotto.BufferUsageType
import mr.robotto.GLWrap
import mr.robotto.MrRenderingContext
import mr.robotto.components.MrComponent

@Serializable
class MrBuffer: MrComponent() {

    @Transient
    lateinit var buffer: GLWrap

    @ExperimentalSerializationApi
    @ByteString
    private lateinit var dataArray: ByteArray
    private var targetBuffer: BufferTargetType = BufferTargetType.ARRAY_BUFFER
    private var usage: BufferUsageType = BufferUsageType.STATIC_DRAW


    override fun initialize(ctx: MrRenderingContext) {
        buffer = ctx.createBuffer()
        // TODO Check errors
        //        if (!data.buffer) {
        //            throw Error("Unable to create buffer")
        //        }

        ctx.bindBuffer(targetBuffer, buffer)
        ctx.bufferData(targetBuffer, dataArray, usage)
    }

    override fun render(ctx: MrRenderingContext) {
    }
}
