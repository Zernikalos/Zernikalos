package mr.robotto.components.mesh

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.Types
import mr.robotto.components.*

@Serializable
class MrAttributeKey(private val index: Int, private val size: Int, private val count: Int, private val normalized: Boolean, private val offset: Int, private val stride: Int): MrComponent() {

    override fun renderInitialize() {
        context.enableVertexAttrib(index)
        context.vertexAttribPointer(index, size, Types.FLOAT.value, normalized, stride, offset)
    }

    override fun render() {
    }

}
