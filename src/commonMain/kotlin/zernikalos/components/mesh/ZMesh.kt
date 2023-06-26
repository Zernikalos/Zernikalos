package zernikalos.components.mesh

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.DrawModes
import zernikalos.Types
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponent
import zernikalos.components.buffer.ZBuffer
import zernikalos.components.buffer.ZIndicesBuffer
import zernikalos.components.buffer.ZVertexArray

@Serializable
class ZMesh: ZComponent() {
    @ProtoNumber(1)
    private lateinit var bufferKeys: Map<String, ZBufferKey>
    @ProtoNumber(2)
    private var indices: ZIndicesBuffer? = null
    @ProtoNumber(3)
    private lateinit var buffers: Map<String, ZBuffer>

    @Transient
    val vao: ZVertexArray = ZVertexArray()

    val useIndexBuffer: Boolean
        get() = indices != null

    override fun initialize(ctx: ZRenderingContext) {
        vao.initialize(ctx)

        bufferKeys.forEach { (name, attr) ->
            val buffer = buffers[name]
            buffer?.initialize(ctx)
            attr.initialize(ctx)
        }

        if (useIndexBuffer) {
            indices?.initialize(ctx)
        }
    }

    override fun render(ctx: ZRenderingContext) {
        vao.render(ctx)
        if (useIndexBuffer) {
            val count = indices?.count!!
            ctx.drawElements(DrawModes.TRIANGLES.value, count, Types.UNSIGNED_SHORT.value, 0)
        } else {
            // TODO: Fix this
            val count = bufferKeys["position"]?.count!!
            ctx.drawArrays(DrawModes.TRIANGLES.value, 0, count)
        }
    }

}
