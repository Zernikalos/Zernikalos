package zernikalos.components

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import zernikalos.ZRenderingContext

abstract class ZComponent<T: ZComponentData, R: ZComponentRender<T>> {

    lateinit var data: T
    lateinit var renderer: R

    abstract fun initialize(ctx: ZRenderingContext)

}

abstract class ZComponentData {

}

interface ZComponentRender<T: ZComponentData> {

    fun initialize(ctx: ZRenderingContext, data: T)

    fun bind(ctx: ZRenderingContext, data: T) {}

    fun unbind(ctx: ZRenderingContext, data: T) {}

    fun render(ctx: ZRenderingContext, data: T) {}
}

abstract class ZComponentSerializer<
    T: ZComponent<D, R>,
    D: ZComponentData,
    R: ZComponentRender<D>>
    : KSerializer<T> {

    abstract val deserializationStrategy: DeserializationStrategy<D>

    override val descriptor: SerialDescriptor
        get() = deserializationStrategy.descriptor

    protected abstract fun createComponentInstance(data: D, renderer: R): T

    protected abstract fun createRendererComponent(): R

    override fun deserialize(decoder: Decoder): T {
        val data = decoder.decodeSerializableValue(deserializationStrategy)
        val renderer = createRendererComponent()
        val component: T = createComponentInstance(data, renderer)
        component.data = data
        component.renderer = renderer
        return component
    }

    override fun serialize(encoder: Encoder, value: T) {}

}

interface ZBindeable {

    fun bind(ctx: ZRenderingContext)

    fun unbind(ctx: ZRenderingContext)

}

interface ZRenderizable {

    fun render(ctx: ZRenderingContext)

}
