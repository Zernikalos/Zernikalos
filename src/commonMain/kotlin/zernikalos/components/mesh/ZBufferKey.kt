package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.components.*
import kotlin.js.JsExport

/**
 * BufferKeys are the one in charge to provide specific data about how the information is being stored inside
 * the RawBuffer data arrays
 */
@Serializable(with = ZBufferKeySerializer::class)
@JsExport
class ZBufferKey: ZBaseComponent<ZBufferKeyData>() {

    /**
     * ID for this BufferKey.
     * Might differ from BufferId
     */
    val id: Int
        get() = data.id

    /**
     * Type of data stored
     *
     */
    val dataType: ZDataType
        get() = data.dataType

    /**
     * How many elements are stored per data unit.
     * Example: a Vec3 will have size equals to 3 in the same way a Scalar will be 1
     */
    val size: Int
        get() = data.size

    /**
     * How many elements of this type are stored.
     * Example: If we store 15 Vec3 elements in the data array the count will have a value of 15.
     */
    val count: Int
        get() = data.count

    val normalized: Boolean
        get() = data.normalized

    val offset: Int
        get() = data.offset

    /**
     * If the data is tightly represented within the array how many elements it requires to be jumped to the next one
     * Example: We store a Vec3 postion and a Vec3 normal in the very same array, the stride will be 6
     */
    val stride: Int
        get() = data.stride

    val isIndexBuffer: Boolean
        get() = data.isIndexBuffer

    /**
     * Refers to the RawBuffer Id @see ZRawBuffer
     */
    val bufferId: Int
        get() = data.bufferId

}

@Serializable
@JsExport
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
