package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Mesh will provide:
 * A relationship between the BufferKey and its RawBuffers in a more cohesive way providing just Buffers
 */
@Serializable(with = ZMeshSerializer::class)
@JsExport
class ZMesh internal constructor(data: ZMeshData, renderer: ZMeshRenderer): ZComponent<ZMeshData, ZMeshRenderer>(data, renderer), ZBindeable, ZRenderizable {

    @JsName("init")
    constructor(): this(ZMeshData(), ZMeshRenderer())

    /**
     * The buffers expressed in a more cohesive way providing key + buffer data in one place
     */
    val buffers: Map<String, ZBuffer>
        get() = data.buffers

    val indexBuffer: ZBuffer?
        get() = data.indexBuffer

    val hasIndexBuffer: Boolean
        get() = data.hasIndexBuffer

    override fun initialize(ctx: ZRenderingContext) {
        buildBuffers()
        renderer.initialize(ctx, data)
    }

    override fun bind(ctx: ZRenderingContext) {
        renderer.bind(ctx, data)
    }

    override fun render(ctx: ZRenderingContext) {
        renderer.render(ctx, data)
    }

    override fun unbind(ctx: ZRenderingContext) {
        renderer.unbind(ctx, data)
    }

    fun addBufferKey(bufferKey: ZBufferKey) {
        data.addBufferKey(bufferKey)
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

}

@Serializable
class ZMeshData(
    @ProtoNumber(1)
    var bufferKeys: ArrayList<ZBufferKey> = arrayListOf(),
    @ProtoNumber(2)
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

expect class ZMeshRenderer(): ZComponentRender<ZMeshData> {

    override fun initialize(ctx: ZRenderingContext, data: ZMeshData)

    override fun render(ctx: ZRenderingContext, data: ZMeshData)

}

class ZMeshSerializer: ZComponentSerializer<ZMesh, ZMeshData, ZMeshRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZMeshData>
        get() = ZMeshData.serializer()

    override fun createRendererComponent(): ZMeshRenderer {
        return ZMeshRenderer()
    }

    override fun createComponentInstance(data: ZMeshData, renderer: ZMeshRenderer): ZMesh {
        return ZMesh(data, renderer)
    }

}
