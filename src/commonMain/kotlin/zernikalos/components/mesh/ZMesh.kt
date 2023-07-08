package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.DrawModes
import zernikalos.Types
import zernikalos.ZRenderingContext
import zernikalos.components.*
import zernikalos.components.buffer.ZBuffer
import zernikalos.components.buffer.ZVertexArray

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

class ZMeshRenderer: ZComponentRender<ZMeshData> {

    @Transient
    val vao: ZVertexArray = ZVertexArray()

    fun useIndexBuffer(data: ZMeshData): Boolean = data.indices != null
    override fun initialize(ctx: ZRenderingContext, data: ZMeshData) {
        vao.initialize(ctx)

        data.bufferKeys.forEach { (name, attr) ->
            val buffer = data.buffers[name]
            buffer?.initialize(ctx)
            attr.initialize(ctx)
        }

        if (useIndexBuffer(data)) {
            data.indices?.initialize(ctx)
        }
    }

    override fun render(ctx: ZRenderingContext, data: ZMeshData) {
        vao.bind(ctx)
        if (useIndexBuffer(data)) {
            val count = data.indices?.count!!
            ctx.drawElements(DrawModes.TRIANGLES.value, count, Types.UNSIGNED_SHORT.value, 0)
        } else {
            // TODO: Fix this
            val count = data.bufferKeys["position"]?.count!!
            ctx.drawArrays(DrawModes.TRIANGLES.value, 0, count)
        }
        vao.unbind(ctx)
    }

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
