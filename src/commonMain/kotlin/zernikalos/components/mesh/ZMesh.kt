package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZRenderingContext
import zernikalos.components.*
import zernikalos.components.buffer.ZBuffer
import kotlin.js.JsExport

@JsExport
@Serializable(with = ZMeshSerializer::class)
class ZMesh: ZComponent<ZMeshData, ZMeshRenderer>(), ZRenderizable {

    val bufferKeys: Map<String, ZBufferKey>
        get() = data.bufferKeys

    val indices: ZBuffer?
        get() = data.indices

    val buffers: Map<String, ZBuffer>
        get() = data.buffers

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

    override fun render(ctx: ZRenderingContext) {
        renderer.render(ctx, data)
    }

}

@Serializable
data class ZMeshData(
    @ProtoNumber(1)
    var bufferKeys: Map<String, ZBufferKey>,
    @ProtoNumber(2)
    val indices: ZBuffer? = null,
    @ProtoNumber(3)
    var buffers: Map<String, ZBuffer>
): ZComponentData()

expect class ZMeshRenderer(): ZComponentRender<ZMeshData> {

    fun useIndexBuffer(data: ZMeshData): Boolean
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
