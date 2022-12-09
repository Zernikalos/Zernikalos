package mr.robotto.objects

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mr.robotto.MrRenderingContext
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@ExperimentalJsExport
@Serializable
@Polymorphic
abstract class MrObject {
    lateinit var name: String

    @JsName("children")
    lateinit var children: Array<@Polymorphic MrObject>

    abstract fun initialize(ctx: MrRenderingContext)
}

@Serializable
open class MrObjectData {
    lateinit var name: String

    @OptIn(ExperimentalJsExport::class)
    lateinit var children: Array<@Polymorphic MrObject>
}

abstract class MrNodeSerializer<T: MrObject, E: MrObjectData>: KSerializer<T> {

    abstract val dataDeserializationStrategy: DeserializationStrategy<E>

    override val descriptor: SerialDescriptor
        get() = dataDeserializationStrategy.descriptor

    abstract fun createDeserializationInstance(): T

    abstract fun assignMembers(data: T, surrogate: E)

    override fun deserialize(decoder: Decoder): T {
        val surrogate = decoder.decodeSerializableValue(dataDeserializationStrategy)
        val data: T = createDeserializationInstance()
        data.name = surrogate.name
        data.children = surrogate.children
        assignMembers(data, surrogate)
        return data
    }

    override fun serialize(encoder: Encoder, value: T) {
    }

}
