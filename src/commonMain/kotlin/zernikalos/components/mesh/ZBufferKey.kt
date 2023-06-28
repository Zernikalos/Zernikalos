package zernikalos.components.mesh

import kotlinx.serialization.Serializable
import zernikalos.Types
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponent

@Serializable
class ZBufferKey(private val id: Int, private val dataType: Types, private val size: Int, val count: Int, private val normalized: Boolean, private val offset: Int, private val stride: Int): ZComponent() {

    override fun initialize(ctx: ZRenderingContext) {
        ctx.enableVertexAttrib(id)
        ctx.vertexAttribPointer(id, size, dataType.value, normalized, stride, offset)
    }

}
