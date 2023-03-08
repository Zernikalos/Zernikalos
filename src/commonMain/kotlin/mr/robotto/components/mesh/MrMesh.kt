package mr.robotto.components.mesh

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import mr.robotto.DrawModes
import mr.robotto.MrRenderingContext
import mr.robotto.Types
import mr.robotto.components.*
import mr.robotto.components.buffer.MrBuffer
import mr.robotto.components.buffer.MrVertexArray

@Serializable
class MrMesh: MrComponent() {
    @ProtoNumber(1)
    private lateinit var attributeKeys: Map<String, MrAttributeKey>
    @ProtoNumber(2)
    private lateinit var indices: MrBuffer
    @ProtoNumber(3)
    private lateinit var vertices: Map<String, MrBuffer>

    @Transient
    val vao: MrVertexArray = MrVertexArray()

    val useIndexBuffer: Boolean
        get() = indices.hasData

    override fun initialize(ctx: MrRenderingContext) {
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

    override fun render(ctx: MrRenderingContext) {
        vao.render(ctx)
        if (useIndexBuffer) {
            ctx.drawElements(DrawModes.TRIANGLES.value, indices.count, Types.UNSIGNED_SHORT.value, 0)
        } else {
            // TODO: Fix this
            ctx.drawArrays(DrawModes.TRIANGLES.value, 0, vertices["position"]?.count!!)
        }
    }

}
