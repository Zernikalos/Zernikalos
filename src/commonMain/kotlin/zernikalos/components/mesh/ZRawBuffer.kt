package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.*
import kotlin.js.JsExport

@JsExport
@Serializable(with = ZRawBufferSerializer::class)
open class ZRawBuffer: ZBaseComponent<ZRawBufferData>() {

    val id: Int
        get() = data.id

    val dataArray: ByteArray
        get() = data.dataArray

    val hasData: Boolean
        get() = data.hasData

}

@Serializable
open class ZRawBufferData(
    @ProtoNumber(1)
    var id: Int,
    @ProtoNumber(2)
    var dataArray: ByteArray
): ZComponentData() {
    val hasData: Boolean
        get() = !dataArray.isEmpty()
}

class ZRawBufferSerializer: ZBaseComponentSerializer<ZRawBuffer, ZRawBufferData>() {
    override val deserializationStrategy: DeserializationStrategy<ZRawBufferData>
        get() = ZRawBufferData.serializer()

    override fun createComponentInstance(data: ZRawBufferData): ZRawBuffer {
        return ZRawBuffer()
    }
}

