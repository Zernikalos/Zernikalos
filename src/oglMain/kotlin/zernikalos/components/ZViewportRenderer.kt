package zernikalos.components

import zernikalos.context.CullModeType
import zernikalos.context.Enabler
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext

actual class ZViewportRenderer: ZComponentRender<ZViewportData> {
    actual override fun initialize(ctx: ZRenderingContext, data: ZViewportData) {
        ctx as ZGLRenderingContext
        ctx.viewport(0, 0, 700, 700)
        ctx.enable(Enabler.DEPTH_TEST.value)
        ctx.cullFace(CullModeType.FRONT.value)
    }

    actual override fun render(ctx: ZRenderingContext, data: ZViewportData) {
        ctx as ZGLRenderingContext
        val v = data.clearColor
        ctx.clearColor(v.x, v.y, v.z, v.w)
        ctx.clear(data.clearMask)
    }

}