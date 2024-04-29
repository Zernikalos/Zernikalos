package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZRenderingContext
import zernikalos.components.*
import zernikalos.components.shader.ZAttributeId
import zernikalos.logger.ZLoggable
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Mesh will provide:
 * A relationship between the BufferKey and its RawBuffers in a more cohesive way providing just Buffers
 */
@Serializable(with = ZMeshSerializer::class)
@JsExport
class ZMesh internal constructor(data: ZMeshData): ZComponent<ZMeshData, ZMeshRenderer>(data), ZBindeable, ZRenderizable, ZRef, ZLoggable {

    @JsName("init")
    constructor(): this(ZMeshData())

    /**
     * The buffers expressed in a more cohesive way providing key + buffer data in one place
     */
    val buffers: Map<String, ZBuffer> by data::buffers

    val indexBuffer: ZBuffer? by data::indexBuffer

    val hasIndexBuffer: Boolean by data::hasIndexBuffer

    override fun createRenderer(ctx: ZRenderingContext): ZMeshRenderer {
        return ZMeshRenderer(ctx, data)
    }

    override fun bind() {
        renderer.bind()
    }

    override fun render() {
        renderer.render()
    }

    override fun unbind() {
        renderer.unbind()
    }

    fun addBufferKey(bufferKey: ZBufferKey) {
        data.addBufferKey(bufferKey)
    }

    fun getBufferByName(name: String): ZBuffer? {
        return data.buffers[name]
    }

    @JsExport.Ignore
    fun getBufferById(attrId: ZAttributeId): ZBuffer? {
        return data.buffers.values.find {
            attrId == it.attributeId
        }
    }

    fun hasBufferKey(name: String): Boolean {
        return data.hasBufferKey(name)
    }

    fun getBufferKey(name: String): ZBufferKey? {
        return data.getBufferKey(name)
    }

    fun addRawBuffer(rawBuffer: ZRawBuffer) {
        data.addRawBuffer(rawBuffer)
    }

    fun buildBuffers() {
        data.buildBuffers()
    }

    override var refId: Int
        get() = computeRefIdFromString(data.toString())
        set(value) {}

}

@Serializable
data class ZMeshData(
    @ProtoNumber(101)
    var bufferKeys: ArrayList<ZBufferKey> = arrayListOf(),
    @ProtoNumber(102)
    var rawBuffers: ArrayList<ZRawBuffer> = arrayListOf()
): ZComponentData() {

    @Transient
    val buffers: HashMap<String, ZBuffer> = HashMap()

    val indexBuffer: ZBuffer?
        get() = buffers.values.find { it.isIndexBuffer }

    val hasIndexBuffer: Boolean
        get() = indexBuffer != null

    fun addBufferKey(bufferKey: ZBufferKey) {
        bufferKeys.add(bufferKey)
    }

    fun getBufferKey(name: String): ZBufferKey? {
        return bufferKeys.find { it.name == name }
    }

    fun hasBufferKey(name: String): Boolean {
        return getBufferKey(name) != null
    }

    fun addRawBuffer(rawBuffer: ZRawBuffer) {
        rawBuffers.add(rawBuffer)
    }

    internal fun buildBuffers() {
        bufferKeys.forEach { key ->
            val buffer = buildBufferForKey(key)
            if (buffer != null) {
                buffers[key.name] = buffer
            }
        }
    }

    private fun buildBufferForKey(key: ZBufferKey): ZBuffer? {
        val rawBuffer = findBufferByKey(key) ?: return null
        return ZBuffer(key, rawBuffer)
    }

    private fun findBufferByKey(key: ZBufferKey): ZRawBuffer? {
        return rawBuffers.find { it.id == key.bufferId }
    }
}

expect class ZMeshRenderer(ctx: ZRenderingContext, data: ZMeshData): ZComponentRender<ZMeshData> {

    override fun initialize()

    override fun render()

}

class ZMeshSerializer: ZComponentSerializer<ZMesh, ZMeshData>() {
    override val deserializationStrategy: DeserializationStrategy<ZMeshData>
        get() = ZMeshData.serializer()

    override fun createComponentInstance(data: ZMeshData): ZMesh {
        return ZMesh(data)
    }

}
