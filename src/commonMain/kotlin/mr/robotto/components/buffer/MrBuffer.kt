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

    private var targetBuffer: BufferTargetType = BufferTargetType.ARRAY_BUFFER
    private var usage: BufferUsageType = BufferUsageType.STATIC_DRAW
    var itemSize: Int = 0
    var count: Int = 0
    @ExperimentalSerializationApi
    @ByteString
    private var dataArray: ByteArray = byteArrayOf()

    val hasData: Boolean
        get() = !dataArray.isEmpty()

    val isIndexBuffer: Boolean
        get() = targetBuffer == BufferTargetType.ELEMENT_ARRAY_BUFFER

    override fun initialize(ctx: MrRenderingContext) {
        if (!hasData) {
            return
        }

        buffer = ctx.createBuffer()
        // TODO Check errors
        //        if (!data.buffer) {
        //            throw Error("Unable to create buffer")
        //        }

        ctx.bindBuffer(targetBuffer, buffer)
        ctx.bufferData(targetBuffer, dataArray, usage)
    }

    fun bind(ctx: MrRenderingContext) {
        ctx.bindBuffer(targetBuffer, buffer)
    }

    override fun render(ctx: MrRenderingContext) {
    }
}
