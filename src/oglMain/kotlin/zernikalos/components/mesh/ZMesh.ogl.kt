package zernikalos.components.mesh

import kotlinx.serialization.Transient
import zernikalos.context.DrawModes
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger
import zernikalos.toOglBaseType
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
actual open class ZMeshRender: ZBaseMesh {

    @JsExport.Ignore
    @Transient
    val vao: ZVertexArray = ZVertexArray()

    @JsName("init")
    actual constructor(): super()

    @JsName("initWithData")
    actual constructor(data: ZMeshData): super(data)

    override fun internalRenderInitialize(ctx: ZRenderingContext) {
        vao.initialize(ctx)

        buffers.values.filter {buff ->
            buff.enabled
        }.forEach { buff ->
            buff.initialize(ctx)
            buff.bind(ctx)
        }
    }

    actual override fun bind(ctx: ZRenderingContext) {
    }

    actual override fun unbind(ctx: ZRenderingContext) {
    }

    actual override fun render(ctx: ZRenderingContext) {
        ctx as ZGLRenderingContext

        val drawMode = convertDrawMode(drawMode)
        vao.bind(ctx)
        if (hasIndexBuffer) {
            logger.debugOnce("Using indexed buffer rendering")
            val indexBuffer = indexBuffer!!
            val count = indexBuffer.count
            ctx.drawElements(drawMode.value, count, toOglBaseType(indexBuffer.dataType), 0)
        } else {
            logger.debugOnce("Using vertices list rendering")
            // TODO: Fix this
            val count = buffers["position"]?.count!!
            ctx.drawArrays(drawMode.value, 0, count)
        }
        vao.unbind(ctx)
    }

}

fun convertDrawMode(drawMode: ZDrawMode): DrawModes = when (drawMode) {
    ZDrawMode.LINES -> DrawModes.LINES
    ZDrawMode.TRIANGLES -> DrawModes.TRIANGLES
    else -> DrawModes.TRIANGLES
}