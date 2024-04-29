package zernikalos.components.mesh

import zernikalos.ZDataType
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponent
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentRender
import zernikalos.components.shader.ZAttributeId
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Utility class for representing the mix of a ZBufferKey + ZRawBuffer in a simpler way
 * Notice that ZBufferKey will only address one ZRawBuffer, however one ZRawBuffer can be addressed by more than one ZBufferKey
 */
@JsExport
class ZBuffer internal constructor(data: ZBufferData): ZComponent<ZBufferData, ZBufferRenderer>(data), ZBindeable {

    @JsName("init")
    constructor(): this(ZBufferData())

    @JsName("initWithArgs")
    constructor(key: ZBufferKey, buffer: ZRawBuffer): this(ZBufferData(key, buffer))

    var enabled: Boolean = false
    
    @JsExport.Ignore
    val attributeId: ZAttributeId
        get() = ZAttributeId.entries.find {
            id == it.id
        }!!

    /**
     * ID for this Buffer.
     */
    val id: Int by data::id

    val isIndexBuffer: Boolean by data::isIndexBuffer

    /**
     * Type of data stored
     *
     */
    val dataType: ZDataType by data.key::dataType

    val name: String by data.key::name

    /**
     * How many elements are stored per data unit.
     * Example: a Vec3 will have size equals to 3 in the same way a Scalar will be 1
     */
    val size: Int by data.key::size

    /**
     * How many elements of this type are stored.
     * Example: If we store 15 Vec3 elements in the data array the count will have a value of 15.
     */
    val count: Int by data.key::count

    val normalized: Boolean by data.key::normalized

    val offset: Int by data.key::offset

    /**
     * If the data is tightly represented within the array how many elements it requires to be jumped to the next one
     * Example: We store a Vec3 postion and a Vec3 normal in the very same array, the stride will be 6
     */
    val stride: Int by data.key::stride


    /**
     * Refers to the RawBuffer Id @see ZRawBuffer
     */
    val bufferId: Int by data.buffer::id

    val dataArray: ByteArray by data.buffer::dataArray

    override fun createRenderer(ctx: ZRenderingContext): ZBufferRenderer {
        return ZBufferRenderer(ctx, data)
    }

    override fun bind() {
        renderer.bind()
    }

    override fun unbind() {
        renderer.unbind()
    }

}

@JsExport
data class ZBufferData(
    val key: ZBufferKey = ZBufferKey(),
    val buffer: ZRawBuffer = ZRawBuffer()
): ZComponentData() {

    val id: Int by key::id

    val isIndexBuffer: Boolean by key::isIndexBuffer
}

expect class ZBufferRenderer(ctx: ZRenderingContext, data: ZBufferData): ZComponentRender<ZBufferData> {
    override fun initialize()

    override fun bind()

    override fun unbind()
}