package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.ZRenderingContext
import zernikalos.components.*

@Serializable(with = ZBufferKeySerializer::class)
class ZBufferKey: ZBaseComponent<ZBufferKeyData>() {

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

    val isIndexBuffer: Boolean
        get() = data.isIndexBuffer

    val bufferId: Int
        get() = data.bufferId

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
    val stride: Int,
    @ProtoNumber(8)
    val isIndexBuffer: Boolean,
    @ProtoNumber(9)
    val bufferId: Int
): ZComponentData()

class ZBufferKeySerializer: ZBaseComponentSerializer<ZBufferKey, ZBufferKeyData>() {
    override val deserializationStrategy: DeserializationStrategy<ZBufferKeyData>
        get() = ZBufferKeyData.serializer()

    override fun createComponentInstance(data: ZBufferKeyData): ZBufferKey {
        return ZBufferKey()
    }

}
