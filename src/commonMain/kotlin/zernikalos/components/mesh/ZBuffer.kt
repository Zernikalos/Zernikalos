package zernikalos.components.mesh

import zernikalos.ZDataType
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponent
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentRender
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Utility class for representing the mix of a ZBufferKey + ZRawBuffer in a simpler way
 * Notice that ZBufferKey will only address one ZRawBuffer, however one ZRawBuffer can be addressed by more than one ZBufferKey
 */
@JsExport
class ZBuffer internal constructor(data: ZBufferData, renderer: ZBufferRenderer): ZComponent<ZBufferData, ZBufferRenderer>(data, renderer), ZBindeable {

    @JsName("init")
    constructor(): this(ZBufferData(), ZBufferRenderer())

    @JsName("initWithArgs")
    constructor(key: ZBufferKey, buffer: ZRawBuffer): this(ZBufferData(key, buffer), ZBufferRenderer())

    /**
     * ID for this Buffer.
     */
    val id: Int
        get() = data.key.id

    val isIndexBuffer: Boolean
        get() = data.isIndexBuffer

    /**
     * Type of data stored
     *
     */
    val dataType: ZDataType
        get() = data.key.dataType

    val name: String
        get() = data.key.name

    /**
     * How many elements are stored per data unit.
     * Example: a Vec3 will have size equals to 3 in the same way a Scalar will be 1
     */
    val size: Int
        get() = data.key.size

    /**
     * How many elements of this type are stored.
     * Example: If we store 15 Vec3 elements in the data array the count will have a value of 15.
     */
    val count: Int
        get() = data.key.count

    val normalized: Boolean
        get() = data.key.normalized

    val offset: Int
        get() = data.key.offset

    /**
     * If the data is tightly represented within the array how many elements it requires to be jumped to the next one
     * Example: We store a Vec3 postion and a Vec3 normal in the very same array, the stride will be 6
     */
    val stride: Int
        get() = data.key.stride


    /**
     * Refers to the RawBuffer Id @see ZRawBuffer
     */
    val bufferId: Int
        get() = data.buffer.id

    val dataArray: ByteArray
        get() = data.buffer.dataArray


    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

    override fun bind(ctx: ZRenderingContext) {
        renderer.bind(ctx, data)
    }

    override fun unbind(ctx: ZRenderingContext) {
        renderer.unbind(ctx, data)
    }

}

@JsExport
data class ZBufferData(
    val key: ZBufferKey = ZBufferKey(),
    val buffer: ZRawBuffer = ZRawBuffer()
): ZComponentData() {

    val id: Int
        get() = key.id

    val isIndexBuffer: Boolean
        get() = key.isIndexBuffer
}

expect class ZBufferRenderer(): ZComponentRender<ZBufferData> {
    override fun initialize(ctx: ZRenderingContext, data: ZBufferData)

    override fun bind(ctx: ZRenderingContext, data: ZBufferData)

    override fun unbind(ctx: ZRenderingContext, data: ZBufferData)
}