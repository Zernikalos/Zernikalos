package zernikalos.components.mesh

import kotlinx.serialization.Transient
import zernikalos.*
import zernikalos.components.ZComponentRender

actual class ZMeshRenderer: ZComponentRender<ZMeshData> {

    @Transient
    val vao: ZVertexArray = ZVertexArray()

    actual override fun initialize(ctx: ZRenderingContext, data: ZMeshData) {
        vao.initialize(ctx)

        data.bufferKeys.forEach { (name, key) ->
            val buffer = data.findBufferByKey(key)
            if (key.isIndexBuffer) {
                buffer?.initializeIndexBuffer(ctx)
            } else {
                buffer?.initializeVertexBuffer(ctx)
            }
            key.initialize(ctx)
        }
    }

    actual override fun render(ctx: ZRenderingContext, data: ZMeshData) {
        ctx as ZGLRenderingContext

        vao.bind(ctx)
        if (data.hasIndexBuffer) {
            val count = data.indexBufferKey?.count!!
            ctx.drawElements(DrawModes.TRIANGLES.value, count, toOglType(ZDataType.UNSIGNED_SHORT), 0)
        } else {
            // TODO: Fix this
            val count = data.bufferKeys["position"]?.count!!
            ctx.drawArrays(DrawModes.TRIANGLES.value, 0, count)
        }
        vao.unbind(ctx)
    }

}