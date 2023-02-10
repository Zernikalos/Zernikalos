package mr.robotto.components.mesh

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.MrRenderingContext
import mr.robotto.Types
import mr.robotto.components.*

@Serializable
class MrAttributeKey(private val index: Int, private val size: Int, private val count: Int, private val normalized: Boolean, private val offset: Int, private val stride: Int): MrComponent() {

    override fun initialize(ctx: MrRenderingContext) {
        ctx.enableVertexAttrib(index)
        ctx.vertexAttribPointer(index, size, Types.FLOAT.value, normalized, stride, offset)
    }

    override fun render(ctx: MrRenderingContext) {
    }

}
