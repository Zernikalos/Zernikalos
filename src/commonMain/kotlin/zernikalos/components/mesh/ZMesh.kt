package zernikalos.components.mesh

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.DrawModes
import zernikalos.Types
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponent
import zernikalos.components.buffer.ZBuffer
import zernikalos.components.buffer.ZVertexArray

@Serializable
class ZMesh: ZComponent() {
    @ProtoNumber(1)
    private lateinit var attributeKeys: Map<String, ZAttributeKey>
    @ProtoNumber(2)
    private lateinit var indices: ZBuffer
    @ProtoNumber(3)
    private lateinit var vertices: Map<String, ZBuffer>

    @Transient
    val vao: ZVertexArray = ZVertexArray()

    val useIndexBuffer: Boolean
        get() = indices.hasData

    override fun initialize(ctx: ZRenderingContext) {
        vao.initialize(ctx)

        attributeKeys.forEach { (name, attr) ->
            val buffer = vertices[name]
            buffer?.initialize(ctx)
            attr.initialize(ctx)
        }

        if (useIndexBuffer) {
            indices.initialize(ctx)
        }
    }

    override fun render(ctx: ZRenderingContext) {
        vao.render(ctx)
        if (useIndexBuffer) {
            ctx.drawElements(DrawModes.TRIANGLES.value, indices.count, Types.UNSIGNED_SHORT.value, 0)
        } else {
            // TODO: Fix this
            ctx.drawArrays(DrawModes.TRIANGLES.value, 0, vertices["position"]?.count!!)
        }
    }

}
