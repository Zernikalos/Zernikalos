package zernikalos.components.mesh

import kotlinx.serialization.Transient
import zernikalos.DrawModes
import zernikalos.Types
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender
import zernikalos.components.buffer.ZVertexArray

actual class ZMeshRenderer: ZComponentRender<ZMeshData> {

    @Transient
    val vao: ZVertexArray = ZVertexArray()

    actual fun useIndexBuffer(data: ZMeshData): Boolean = data.indices != null
    actual override fun initialize(ctx: ZRenderingContext, data: ZMeshData) {
        vao.initialize(ctx)

        data.bufferKeys.forEach { (name, attr) ->
            val buffer = data.buffers[name]
            buffer?.initialize(ctx)
            attr.initialize(ctx)
        }

        if (useIndexBuffer(data)) {
            data.indices?.initialize(ctx)
        }
    }

    actual override fun render(ctx: ZRenderingContext, data: ZMeshData) {
        vao.bind(ctx)
        if (useIndexBuffer(data)) {
            val count = data.indices?.count!!
            ctx.drawElements(DrawModes.TRIANGLES.value, count, Types.UNSIGNED_SHORT.value, 0)
        } else {
            // TODO: Fix this
            val count = data.bufferKeys["position"]?.count!!
            ctx.drawArrays(DrawModes.TRIANGLES.value, 0, count)
        }
        vao.unbind(ctx)
    }

}