package zernikalos.components.mesh

import kotlinx.serialization.Serializable
import zernikalos.Types
import zernikalos.ZkRenderingContext
import zernikalos.components.ZkComponent

@Serializable
class ZkAttributeKey(private val index: Int, private val size: Int, private val count: Int, private val normalized: Boolean, private val offset: Int, private val stride: Int): ZkComponent() {

    override fun initialize(ctx: ZkRenderingContext) {
        ctx.enableVertexAttrib(index)
        ctx.vertexAttribPointer(index, size, Types.FLOAT.value, normalized, stride, offset)
    }

    override fun render(ctx: ZkRenderingContext) {
    }

}
