package zernikalos.components.mesh

import zernikalos.ZGLRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZBufferKeyRenderer: ZComponentRender<ZBufferKeyData> {
    actual override fun initialize(ctx: ZRenderingContext, data: ZBufferKeyData) {
        ctx as ZGLRenderingContext

        ctx.enableVertexAttrib(data.id)
        ctx.vertexAttribPointer(
            data.id,
            data.size,
            data.dataType.value,
            data.normalized,
            data.stride,
            data.offset
        )
    }

}