package mr.robotto.components.mesh

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.DrawModes
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

    override fun renderInitialize() {
        vao.initialize(context)

        attributeKeys.forEach { (name, attr) ->
            val buffer = vertices[name]
            buffer?.initialize(context)
            attr.initialize(context)
        }
    }

    override fun render() {
        vao.render()
        context.drawArrays(DrawModes.TRIANGLES.value, 0, 36)
    }

}
