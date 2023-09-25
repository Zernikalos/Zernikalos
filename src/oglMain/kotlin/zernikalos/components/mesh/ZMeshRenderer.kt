package zernikalos.components.mesh

import kotlinx.serialization.Transient
import zernikalos.*
import zernikalos.components.ZComponentRender

actual class ZMeshRenderer: ZComponentRender<ZMeshData> {

    @Transient
    val vao: ZVertexArray = ZVertexArray()

    actual override fun initialize(ctx: ZRenderingContext, data: ZMeshData) {
        vao.initialize(ctx)

        data.buffers.values.forEach { buff ->
            buff.initialize(ctx)
        }
    }

    actual override fun render(ctx: ZRenderingContext, data: ZMeshData) {
        ctx as ZGLRenderingContext

        vao.bind(ctx)
        if (data.hasIndexBuffer) {
            // If we have the index buffer for sure this will not be null
            val indexBuffer = data.indexBuffer!!
            val count = indexBuffer.count
            // TODO: you don't need to draw triangles all the time
            ctx.drawElements(DrawModes.TRIANGLES.value, count, toOglBaseType(indexBuffer.dataType), 0)
        } else {
            // TODO: Fix this
            val count = data.buffers["position"]?.count!!
            ctx.drawArrays(DrawModes.TRIANGLES.value, 0, count)
        }
        vao.unbind(ctx)
    }

}