package mr.robotto.components

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mr.robotto.MrRenderingContext

abstract class MrComponent<D: MrComponentData, R: MrComponentRender<D>> {

    abstract var data: D
    abstract var renderer: R

    fun initialize(ctx: MrRenderingContext) {
        renderer.initialize(ctx, data)
    }

    fun render() {
        renderer.render()
    }
}

@Serializable
abstract class MrComponentData

abstract class MrComponentRender<D: MrComponentData> {

    protected lateinit var context: MrRenderingContext
    protected lateinit var data: D

    fun initialize(ctx: MrRenderingContext, d: D) {
        context = ctx
        data = d
        internalInitialize()
    }

    protected abstract fun internalInitialize()

    abstract fun render()

}

abstract class MrComponentSerializer<T: MrComponent<E, *>, E: MrComponentData>: KSerializer<T> {

    abstract val deserializationStrategy: DeserializationStrategy<E>

    override val descriptor: SerialDescriptor
        get() = deserializationStrategy.descriptor

    abstract fun createDeserializationInstance(): T

    override fun deserialize(decoder: Decoder): T {
        val surrogate = decoder.decodeSerializableValue(deserializationStrategy)
        val data: T = createDeserializationInstance()
        data.data = surrogate
        return data
    }

    override fun serialize(encoder: Encoder, value: T) {}

}