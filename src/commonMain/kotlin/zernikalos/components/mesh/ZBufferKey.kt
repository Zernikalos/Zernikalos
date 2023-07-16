package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.ZRenderingContext
import zernikalos.components.*

@Serializable(with = ZBufferKeySerializer::class)
class ZBufferKey: ZComponent<ZBufferKeyData, ZBufferKeyRenderer>() {

    val id: Int
        get() = data.id

    val dataType: ZDataType
        get() = data.dataType

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
    val dataType: ZDataType,
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

expect class ZBufferKeyRenderer(): ZComponentRender<ZBufferKeyData> {
    override fun initialize(ctx: ZRenderingContext, data: ZBufferKeyData)

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
