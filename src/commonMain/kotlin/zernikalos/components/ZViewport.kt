package zernikalos.components

import zernikalos.BufferBit
import zernikalos.CullModeType
import zernikalos.Enabler
import zernikalos.ZRenderingContext
import zernikalos.math.ZVector4F

class ZViewport: ZComponent() {
    private val viewport: Array<Int> = arrayOf(0, 0, 700, 700)
    private val clearColor: ZVector4F = ZVector4F(.2f, .2f, .2f, 1.0f)
    private val clearMask: Int = BufferBit.COLOR_BUFFER.value or BufferBit.DEPTH_BUFFER.value

    override fun initialize(ctx: ZRenderingContext) {
        val vp = viewport
        ctx.viewport(0, 0, 700, 700)
        ctx.enable(Enabler.DEPTH_TEST.value)
        ctx.cullFace(CullModeType.FRONT.value)
    }

    override fun render(ctx: ZRenderingContext) {
        val v = clearColor
        ctx.clearColor(v.x, v.y, v.z, v.w)
        ctx.clear(clearMask)
    }
}
