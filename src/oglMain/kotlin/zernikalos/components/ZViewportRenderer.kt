package zernikalos.components

import zernikalos.CullModeType
import zernikalos.Enabler
import zernikalos.ZRenderingContext

actual class ZViewportRenderer: ZComponentRender<ZViewportData> {
    actual override fun initialize(ctx: ZRenderingContext, data: ZViewportData) {
        ctx.viewport(0, 0, 700, 700)
        ctx.enable(Enabler.DEPTH_TEST.value)
        ctx.cullFace(CullModeType.FRONT.value)
    }

    actual override fun render(ctx: ZRenderingContext, data: ZViewportData) {
        val v = data.clearColor
        ctx.clearColor(v.x, v.y, v.z, v.w)
        ctx.clear(data.clearMask)
    }

}