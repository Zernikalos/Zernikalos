package zernikalos.components.buffer

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.BufferTargetType
import zernikalos.BufferUsageType
import zernikalos.GLWrap
import zernikalos.ZkRenderingContext
import zernikalos.components.ZkComponent

@Serializable
class ZkBuffer: ZkComponent() {

    @Transient
    lateinit var buffer: GLWrap

    @ProtoNumber(1)
    private var targetBuffer: BufferTargetType = BufferTargetType.ARRAY_BUFFER
    @ProtoNumber(2)
    private var usage: BufferUsageType = BufferUsageType.STATIC_DRAW
    @ProtoNumber(3)
    var itemSize: Int = 0
    @ProtoNumber(4)
    var count: Int = 0
    @ProtoNumber(5)
    private lateinit var dataArray: ByteArray

    val hasData: Boolean
        get() = !dataArray.isEmpty()

    val isIndexBuffer: Boolean
        get() = targetBuffer == BufferTargetType.ELEMENT_ARRAY_BUFFER

    override fun initialize(ctx: ZkRenderingContext) {
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

    fun bind(ctx: ZkRenderingContext) {
        ctx.bindBuffer(targetBuffer, buffer)
    }

    override fun render(ctx: ZkRenderingContext) {
    }
}
