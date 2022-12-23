package mr.robotto.components.mesh

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.DrawModes
import mr.robotto.components.*
import mr.robotto.components.buffer.MrVertexArray

@Serializable
class MrMesh(val indices: Array<Int>, val attributes: Map<String, MrAttribute>): MrComponent() {

    @Transient
    val vao: MrVertexArray = MrVertexArray()

    override fun renderInitialize() {
        vao.initialize(context)
        // attributes.values.forEach { attr -> attr.initialize(context) }
        val attr = attributes["position"]
        attr?.initialize(context)
    }

    override fun render() {
        vao.render()
        context.drawArrays(DrawModes.TRIANGLES.value, 0, 24)
    }

}
