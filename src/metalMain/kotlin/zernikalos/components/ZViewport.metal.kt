package zernikalos.components

import zernikalos.context.ZRenderingContext
import zernikalos.math.ZBox2D
import zernikalos.math.ZColor

actual open class ZViewportRender : ZBaseViewport() {
    actual constructor() {
        TODO("Not yet implemented")
    }

    actual constructor(clearColor: ZColor, viewBox: ZBox2D) {
        TODO("Not yet implemented")
    }

    actual override fun render(ctx: ZRenderingContext) {
    }

    actual override fun onViewportResize(width: Int, height: Int) {
    }

}