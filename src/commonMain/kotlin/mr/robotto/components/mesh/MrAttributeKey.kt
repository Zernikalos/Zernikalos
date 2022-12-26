package mr.robotto.components.mesh

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.Types
import mr.robotto.components.*

@Serializable
class MrAttributeKey(val index: Int, val size: Int, val count: Int, val normalized: Boolean, val offset: Int, val stride: Int): MrComponent() {

    override fun renderInitialize() {
        context.enableVertexAttrib(index)
        context.vertexAttribPointer(index, size, Types.FLOAT.value, normalized, stride, offset)
    }

    override fun render() {
    }

}
