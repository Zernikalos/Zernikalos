package zernikalos.components

import zernikalos.context.*
import zernikalos.math.ZBox2D
import zernikalos.math.ZColor

actual open class ZViewportRender : ZBaseViewport {

    var clearMask: Int = BufferBit.COLOR_BUFFER.value or BufferBit.DEPTH_BUFFER.value

    actual constructor(): super()

    actual constructor(clearColor: ZColor, viewBox: ZBox2D): super(clearColor, viewBox)

    override fun internalRenderInitialize(ctx: ZRenderingContext) {
        ctx as ZGLRenderingContext
        ctx.enable(Enabler.DEPTH_TEST.value)
        ctx.cullFace(CullModeType.FRONT.value)
    }

    actual override fun render(ctx: ZRenderingContext) {
        ctx as ZGLRenderingContext
        val v = clearColor
        ctx.clearColor(v.r, v.g, v.b, v.a)
        ctx.clear(clearMask)
    }

    actual override fun onViewportResize(ctx: ZRenderingContext, width: Int, height: Int) {
        super.onViewportResize(ctx, width, height)

        ctx as ZGLRenderingContext
        ctx.viewport(
            viewBox.top,
            viewBox.left,
            viewBox.width,
            viewBox.height
        )
    }

}