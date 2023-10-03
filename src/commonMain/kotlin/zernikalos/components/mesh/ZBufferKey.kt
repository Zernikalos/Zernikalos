package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.components.*
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * BufferKeys are the one in charge to provide specific data about how the information is being stored inside
 * the RawBuffer data arrays
 */
@Serializable(with = ZBufferKeySerializer::class)
@JsExport
class ZBufferKey internal constructor(data: ZBufferKeyData): ZBaseComponent<ZBufferKeyData>(data) {

    @JsName("init")
    constructor(): this(ZBufferKeyData())

    @JsName("initWithArgs")
    constructor(id: Int, dataType: ZDataType, name: String, size: Int, count: Int, normalized: Boolean, offset: Int, stride: Int, isIndexBuffer: Boolean, bufferId: Int):
        this(ZBufferKeyData(id, dataType, name, size, count, normalized, offset, stride, isIndexBuffer, bufferId))

    /**
     * ID for this BufferKey.
     * Might differ from BufferId
     */
    var id: Int by data::id

    /**
     * Type of data stored
     *
     */
    var dataType: ZDataType by data::dataType

    var name: String by data::name

    /**
     * How many elements are stored per data unit.
     * Example: a Vec3 will have size equals to 3 in the same way a Scalar will be 1
     */
    var size: Int by data::size

    /**
     * How many elements of this type are stored.
     * Example: If we store 15 Vec3 elements in the data array the count will have a value of 15.
     */
    var count: Int by data::count

    var normalized: Boolean by data::normalized

    var offset: Int by data::offset

    /**
     * If the data is tightly represented within the array how many elements it requires to be jumped to the next one
     * Example: We store a Vec3 postion and a Vec3 normal in the very same array, the stride will be 6
     */
    var stride: Int by data::stride

    var isIndexBuffer: Boolean by data::isIndexBuffer

    /**
     * Refers to the RawBuffer Id @see ZRawBuffer
     */
    var bufferId: Int by data::bufferId

}

@Serializable
@JsExport
data class ZBufferKeyData(
    @ProtoNumber(1)
    var id: Int = -1,
    @ProtoNumber(2)
    var dataType: ZDataType = ZTypes.NONE,
    @ProtoNumber(3)
    var name: String = "",
    @ProtoNumber(4)
    var size: Int = -1,
    @ProtoNumber(5)
    var count: Int = -1,
    @ProtoNumber(6)
    var normalized: Boolean = false,
    @ProtoNumber(7)
    var offset: Int = -1,
    @ProtoNumber(8)
    var stride: Int = -1,
    @ProtoNumber(9)
    var isIndexBuffer: Boolean = false,
    @ProtoNumber(10)
    var bufferId: Int = -1
): ZComponentData()

class ZBufferKeySerializer: ZBaseComponentSerializer<ZBufferKey, ZBufferKeyData>() {
    override val deserializationStrategy: DeserializationStrategy<ZBufferKeyData>
        get() = ZBufferKeyData.serializer()

    override fun createComponentInstance(data: ZBufferKeyData): ZBufferKey {
        return ZBufferKey(data)
    }

}
