package zernikalos.components

import zernikalos.context.ZRenderingContext
import libvulkan.*


actual class ZViewportRenderer actual constructor(
    ctx: ZRenderingContext,
    data: ZViewportData
) : ZComponentRenderer(ctx) {
    actual override fun initialize() {
    }

    actual override fun render() {
    }

    actual fun onViewportResize(width: Int, height: Int) {
    }
}
