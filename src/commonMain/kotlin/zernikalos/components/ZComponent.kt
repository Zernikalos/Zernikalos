package zernikalos.components

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import zernikalos.context.ZRenderingContext

abstract class ZComponent<D: ZComponentData, R: ZComponentRender<D>> internal constructor(val data: D) {

    private var initialized: Boolean = false

    val isInitialized: Boolean
        get() = initialized

    private var _renderer: R? = null

    val renderer: R
        get() {
            if (!initialized || _renderer == null) {
                throw Error("The component has not been initialized prior to access the renderer")
            }
            return _renderer!!
        }

    fun initialize(ctx: ZRenderingContext) {
        _renderer = createRenderer(ctx)
        initialized = true

        internalInitialize(ctx)
    }

    protected open fun internalInitialize(ctx: ZRenderingContext) {

    }

    protected open fun createRenderer(ctx: ZRenderingContext): R? {
        return null
    }

}

abstract class ZComponentData {

}

abstract class ZComponentRender<D: ZComponentData>(val ctx: ZRenderingContext, val data: D) {

    abstract fun initialize()

    open fun bind() {}

    open fun unbind() {}

    open fun render() {}
}

abstract class ZComponentSerializer<
    T: ZComponent<D, *>,
    D: ZComponentData>
    : KSerializer<T> {

    abstract val deserializationStrategy: DeserializationStrategy<D>

    override val descriptor: SerialDescriptor
        get() = deserializationStrategy.descriptor

    protected abstract fun createComponentInstance(data: D): T

    override fun deserialize(decoder: Decoder): T {
        val data = decoder.decodeSerializableValue(deserializationStrategy)
        return createComponentInstance(data)
    }

    override fun serialize(encoder: Encoder, value: T) {}

}

interface ZBindeable {

    fun bind()

    fun unbind()

}

interface ZRenderizable {

    fun render()

}
