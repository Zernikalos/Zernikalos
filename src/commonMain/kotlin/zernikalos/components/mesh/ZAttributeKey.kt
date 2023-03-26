package zernikalos.components.mesh

import kotlinx.serialization.Serializable
import zernikalos.Types
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponent

@Serializable
class ZAttributeKey(private val index: Int, private val size: Int, private val count: Int, private val normalized: Boolean, private val offset: Int, private val stride: Int): ZComponent() {

    override fun initialize(ctx: ZRenderingContext) {
        ctx.enableVertexAttrib(index)
        ctx.vertexAttribPointer(index, size, Types.FLOAT.value, normalized, stride, offset)
    }

    override fun render(ctx: ZRenderingContext) {
    }

}
