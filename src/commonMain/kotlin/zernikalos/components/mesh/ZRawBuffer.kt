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
class ZRawBuffer internal constructor(data: ZRawBufferData): ZComponent<ZRawBufferData, ZComponentRender<ZRawBufferData>>(data) {

    @JsName("init")
    constructor(): this(ZRawBufferData())

    @JsName("initWithArgs")
    constructor(id: Int, dataArray: ByteArray): this(ZRawBufferData(id, dataArray))

    var id: Int by data::id

    var dataArray: ByteArray by data::dataArray

    val hasData: Boolean by data::hasData

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

class ZRawBufferSerializer: ZComponentSerializer<ZRawBuffer, ZRawBufferData>() {
    override val deserializationStrategy: DeserializationStrategy<ZRawBufferData>
        get() = ZRawBufferData.serializer()

    override fun createComponentInstance(data: ZRawBufferData): ZRawBuffer {
        return ZRawBuffer(data)
    }
}

