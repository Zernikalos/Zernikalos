package mr.robotto.components.mesh

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.DrawModes
import mr.robotto.MrRenderingContext
import mr.robotto.components.*
import mr.robotto.components.buffer.MrBuffer
import mr.robotto.components.buffer.MrVertexArray

@Serializable
class MrMesh: MrComponent() {
    private lateinit var attributeKeys: Map<String, MrAttributeKey>
    private lateinit var indices: MrBuffer
    private lateinit var vertices: Map<String, MrBuffer>

    @Transient
    val vao: MrVertexArray = MrVertexArray()

    override fun initialize(ctx: MrRenderingContext) {
        vao.initialize(ctx)

        attributeKeys.forEach { (name, attr) ->
            val buffer = vertices[name]
            buffer?.initialize(ctx)
            attr.initialize(ctx)
        }
    }

    override fun render(ctx: MrRenderingContext) {
        vao.render(ctx)
        ctx.drawArrays(DrawModes.TRIANGLES.value, 0, 36)
    }

}
