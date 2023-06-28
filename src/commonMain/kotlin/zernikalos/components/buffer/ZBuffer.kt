package zernikalos.components.buffer

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.BufferTargetType
import zernikalos.BufferUsageType
import zernikalos.GLWrap
import zernikalos.ZRenderingContext
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponent

@Serializable
open class ZBuffer: ZBindeable() {

    @Transient
    lateinit var buffer: GLWrap

    @ProtoNumber(1)
    var isIndexBuffer: Boolean = false

    @ProtoNumber(5)
    private lateinit var dataArray: ByteArray

    val hasData: Boolean
        get() = !dataArray.isEmpty()

    private val targetBuffer: BufferTargetType
        get() = if (isIndexBuffer) BufferTargetType.ELEMENT_ARRAY_BUFFER else BufferTargetType.ARRAY_BUFFER

    override fun initialize(ctx: ZRenderingContext) {
        if (!hasData) {
            return
        }

        buffer = ctx.createBuffer()
        // TODO Check errors
        //        if (!data.buffer) {
        //            throw Error("Unable to create buffer")
        //        }

        ctx.bindBuffer(targetBuffer, buffer)
        ctx.bufferData(targetBuffer, dataArray, BufferUsageType.STATIC_DRAW)
    }

    override fun bind(ctx: ZRenderingContext) {
        ctx.bindBuffer(targetBuffer, buffer)
    }

    override fun unbind(ctx: ZRenderingContext) {
    }

}
