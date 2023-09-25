package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.*
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Raw buffer representing the data array with vertex information stored
 */
@Serializable(with = ZRawBufferSerializer::class)
@JsExport
open class ZRawBuffer internal constructor(data: ZRawBufferData): ZBaseComponent<ZRawBufferData>(data) {

    @JsName("init")
    constructor(): this(ZRawBufferData())

    @JsName("initWithArgs")
    constructor(id: Int, dataArray: ByteArray): this(ZRawBufferData(id, dataArray))

    var id: Int
        get() = data.id
        set(value) {
            data.id = value
        }

    var dataArray: ByteArray
        get() = data.dataArray
        set(value) {
            data.dataArray = value
        }

    val hasData: Boolean
        get() = data.hasData

}

@Serializable
@JsExport
open class ZRawBufferData(
    @ProtoNumber(1)
    var id: Int = -1,
    @ProtoNumber(2)
    var dataArray: ByteArray = byteArrayOf()
): ZComponentData() {
    val hasData: Boolean
        get() = !dataArray.isEmpty()
}

class ZRawBufferSerializer: ZBaseComponentSerializer<ZRawBuffer, ZRawBufferData>() {
    override val deserializationStrategy: DeserializationStrategy<ZRawBufferData>
        get() = ZRawBufferData.serializer()

    override fun createComponentInstance(data: ZRawBufferData): ZRawBuffer {
        return ZRawBuffer(data)
    }
}

