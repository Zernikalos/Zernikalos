package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport

@Serializable(with = ZMeshSerializer::class)
@JsExport
class ZMesh: ZComponent<ZMeshData, ZMeshRenderer>(), ZRenderizable {

    val bufferKeys: Map<String, ZBufferKey>
        get() = data.bufferKeys

    val buffers: Map<String, ZBuffer>
        get() = data.buffers

    val indexBufferKey: ZBufferKey?
        get() = data.indexBufferKey

    val indexBuffer: ZBuffer?
        get() = data.indexBuffer


    val hasIndexBuffer: Boolean
        get() = data.hasIndexBuffer

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

    override fun render(ctx: ZRenderingContext) {
        renderer.render(ctx, data)
    }

}

@Serializable
@JsExport
class ZMeshData(
    @ProtoNumber(1)
    var bufferKeys: Map<String, ZBufferKey>,
    @ProtoNumber(2)
    var rawBuffers: Map<String, ZRawBuffer>
): ZComponentData() {

    @Transient
    val buffers: HashMap<String, ZBuffer> = HashMap()

    init {
        bufferKeys.entries.forEach { (name, key) ->
            val buffer = findBufferByKey(key)
            if (buffer != null) {
                val compBuffer = ZBuffer(key, buffer)
                buffers[name] = compBuffer
            }
        }
    }

    val indexBuffer: ZBuffer?
        get() = buffers.values.find { it -> it.isIndexBuffer }

    val indexBufferKey: ZBufferKey?
        get() = bufferKeys.values.find { it -> it.isIndexBuffer }

    val hasIndexBuffer: Boolean
        get() = indexBufferKey != null


    fun findBufferByKey(key: ZBufferKey): ZRawBuffer? {
        return rawBuffers.values.find { it -> it.id == key.bufferId }
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
        return ZMesh()
    }

}
