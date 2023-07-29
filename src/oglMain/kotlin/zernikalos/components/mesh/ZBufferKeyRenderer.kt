package zernikalos.components.mesh

import zernikalos.ZGLRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender
import zernikalos.toOglType

actual class ZBufferKeyRenderer: ZComponentRender<ZBufferKeyData> {
    actual override fun initialize(ctx: ZRenderingContext, data: ZBufferKeyData) {
        ctx as ZGLRenderingContext

        val glDataType = toOglType(data.dataType)

        ctx.enableVertexAttrib(data.id)
        ctx.vertexAttribPointer(
            data.id,
            data.size,
            glDataType,
            data.normalized,
            data.stride,
            data.offset
        )
    }

}