package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport

@JsExport
@Serializable(with = ZBufferSerializer::class)
open class ZBuffer: ZComponent<ZBufferData, ZBufferRenderer>(), ZBindeable {

    val id: Int
        get() = data.id

    val dataArray: ByteArray
        get() = data.dataArray

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

    fun initializeIndexBuffer(ctx: ZRenderingContext) {
        renderer.initializeIndexBuffer(ctx, data)
    }

    fun initializeVertexBuffer(ctx: ZRenderingContext) {
        renderer.initializeVertexBuffer(ctx, data)
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
    var id: Int,
    @ProtoNumber(2)
    var dataArray: ByteArray
): ZComponentData() {
    val hasData: Boolean
        get() = !dataArray.isEmpty()
}

expect class ZBufferRenderer(): ZComponentRender<ZBufferData> {

    override fun initialize(ctx: ZRenderingContext, data: ZBufferData)

    fun initializeIndexBuffer(ctx: ZRenderingContext, data: ZBufferData)

    fun initializeVertexBuffer(ctx: ZRenderingContext, data: ZBufferData)

    override fun bind(ctx: ZRenderingContext, data: ZBufferData)

    override fun unbind(ctx: ZRenderingContext, data: ZBufferData)

}

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

