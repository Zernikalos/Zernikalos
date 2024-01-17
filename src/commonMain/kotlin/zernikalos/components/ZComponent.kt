package zernikalos.components

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZRenderingContext
import zernikalos.utils.ZLoggable
import zernikalos.utils.crc32
import kotlin.js.JsExport

@JsExport
abstract class ZComponent<D: ZComponentData, R: ZComponentRender<D>> internal constructor(val data: D): ZLoggable {

    var refId: Int by data::refId

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

    val isRenderizable: Boolean
        get() = _renderer != null

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

    override fun toString(): String {
        return data.toString()
    }

}

@JsExport
@Serializable
abstract class ZComponentData: ZLoggable {

    @ProtoNumber(500)
    protected var _refId: Int? = null
    var refId: Int
        get() {
            if (_refId == null) {
                _refId = computeRefId()
            }
            return _refId!!
        }
        set(value) {
            _refId = value
        }

    private fun computeRefId(): Int {
        val dataArray = toString().encodeToByteArray()
        val hashValue = crc32(dataArray)
        return if (hashValue < 0) hashValue.inv() else hashValue
    }

    abstract override fun toString(): String

}

@JsExport
abstract class ZComponentRender<D: ZComponentData>(val ctx: ZRenderingContext, val data: D): ZLoggable {

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
