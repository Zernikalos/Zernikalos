package mr.robotto.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.cbor.ByteString
import mr.robotto.MrRenderingContext
import mr.robotto.components.MrComponent
import mr.robotto.components.MrComponentData
import mr.robotto.components.MrComponentRender
import mr.robotto.components.MrComponentSerializer
import mr.robotto.components.buffer.MrVertexBuffer

@Serializable(with = MrAttributeSerializer::class)
class MrAttribute : MrComponent<MrAttributeData, MrAttributeRender>() {
    override lateinit var data: MrAttributeData
    override var renderer: MrAttributeRender = MrAttributeRender()
}

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class MrAttributeData constructor(val name: String, val size: Int, val count: Int, @ByteString val data: ByteArray): MrComponentData() {
    @Transient
    val vertexBuffer: MrVertexBuffer = MrVertexBuffer()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        other as MrAttributeData
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

class MrAttributeRender: MrComponentRender<MrAttributeData>() {
    override fun internalInitialize() {
        // TODO Create an initializer in base classes
        data.vertexBuffer.data.dataArray = data.data
        data.vertexBuffer.initialize(context)
    }

    override fun render() {}

}

class MrAttributeSerializer: MrComponentSerializer<MrAttribute, MrAttributeData>() {
    override val deserializationStrategy: DeserializationStrategy<MrAttributeData>
        get() = MrAttributeData.serializer()

    override fun createDeserializationInstance(): MrAttribute {
        return MrAttribute()
    }

}
