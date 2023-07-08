package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.Types
import zernikalos.ZRenderingContext
import zernikalos.components.*

@Serializable(with = ZBufferKeySerializer::class)
class ZBufferKey: ZComponent<ZBufferKeyData, ZBufferKeyRenderer>() {

    val id: Int
        get() = data.id

    val dataType: Types
        get() = data.dataType

    // TODO: Review duplicated
    val size: Int
        get() = data.size

    val count: Int
        get() = data.count

    val normalized: Boolean
        get() = data.normalized

    val offset: Int
        get() = data.offset

    val stride: Int
        get() = data.stride

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

}

@Serializable
data class ZBufferKeyData(
    @ProtoNumber(1)
    val id: Int,
    @ProtoNumber(2)
    val dataType: Types,
    @ProtoNumber(3)
    val size: Int,
    @ProtoNumber(4)
    val count: Int,
    @ProtoNumber(5)
    val normalized: Boolean,
    @ProtoNumber(6)
    val offset: Int,
    @ProtoNumber(7)
    val stride: Int
): ZComponentData()

class ZBufferKeyRenderer: ZComponentRender<ZBufferKeyData> {
    override fun initialize(ctx: ZRenderingContext, data: ZBufferKeyData) {
        ctx.enableVertexAttrib(data.id)
        ctx.vertexAttribPointer(
            data.id,
            data.size,
            data.dataType.value,
            data.normalized,
            data.stride,
            data.offset
        )
    }

}

class ZBufferKeySerializer: ZComponentSerializer<ZBufferKey, ZBufferKeyData, ZBufferKeyRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZBufferKeyData>
        get() = ZBufferKeyData.serializer()

    override fun createRendererComponent(): ZBufferKeyRenderer {
        return ZBufferKeyRenderer()
    }

    override fun createComponentInstance(data: ZBufferKeyData, renderer: ZBufferKeyRenderer): ZBufferKey {
        return ZBufferKey()
    }

}
