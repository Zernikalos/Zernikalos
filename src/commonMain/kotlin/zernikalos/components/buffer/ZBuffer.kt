package zernikalos.components.buffer

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.BufferTargetType
import zernikalos.BufferUsageType
import zernikalos.GLWrap
import zernikalos.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport

@JsExport
@Serializable(with = ZBufferSerializer::class)
open class ZBuffer: ZComponent<ZBufferData, ZBufferRenderer>(), ZBindeable {

    val isIndexBuffer: Boolean
        get() = data.isIndexBuffer

    val size: Int
        get() = data.size

    val count: Int
        get() = data.count

    val dataArray: ByteArray
        get() = data.dataArray

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

    override fun bind(ctx: ZRenderingContext) {
        renderer.bind(ctx, data)
    }

    override fun unbind(ctx: ZRenderingContext) {
        renderer.unbind(ctx, data)
    }

}

@Serializable
open class ZBufferData(
    @ProtoNumber(1)
    var isIndexBuffer: Boolean = false,
    @ProtoNumber(3)
    var size: Int = 0,
    @ProtoNumber(4)
    var count: Int = 0,
    @ProtoNumber(5)
    var dataArray: ByteArray
): ZComponentData() {
    val hasData: Boolean
        get() = !dataArray.isEmpty()

    // TODO: Change this in order to make it ogl independent
    val targetBuffer: BufferTargetType
        get() = if (isIndexBuffer) BufferTargetType.ELEMENT_ARRAY_BUFFER else BufferTargetType.ARRAY_BUFFER

}

expect class ZBufferRenderer(): ZComponentRender<ZBufferData> {

    override fun initialize(ctx: ZRenderingContext, data: ZBufferData)

    override fun bind(ctx: ZRenderingContext, data: ZBufferData)

    override fun unbind(ctx: ZRenderingContext, data: ZBufferData)

}

/* class ZBufferRenderer: ZComponentRender<ZBufferData> {

    @Transient
    lateinit var buffer: GLWrap

    override fun initialize(ctx: ZRenderingContext, data: ZBufferData) {
        if (!data.hasData) {
            return
        }

        buffer = ctx.createBuffer()
        // TODO Check errors
        //        if (!data.buffer) {
        //            throw Error("Unable to create buffer")
        //        }

        ctx.bindBuffer(data.targetBuffer, buffer)
        ctx.bufferData(data.targetBuffer, data.dataArray, BufferUsageType.STATIC_DRAW)
    }

    override fun bind(ctx: ZRenderingContext, data: ZBufferData) {
        ctx.bindBuffer(data.targetBuffer, buffer)
    }

    override fun unbind(ctx: ZRenderingContext, data: ZBufferData) {
        super.unbind(ctx, data)
    }

} */

class ZBufferSerializer: ZComponentSerializer<ZBuffer, ZBufferData, ZBufferRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZBufferData>
        get() = ZBufferData.serializer()

    override fun createRendererComponent(): ZBufferRenderer {
        return ZBufferRenderer()
    }

    override fun createComponentInstance(data: ZBufferData, renderer: ZBufferRenderer): ZBuffer {
        return ZBuffer()
    }
}

