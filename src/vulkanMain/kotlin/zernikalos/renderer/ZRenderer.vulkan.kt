package zernikalos.renderer

import zernikalos.context.ZContext

actual class ZRenderer actual constructor(ctx: ZContext) : ZRendererBase(ctx) {
    actual fun bind() {
    }

    actual fun unbind() {
    }

    actual fun render() {
    }

    actual override fun onViewportResize(width: Int, height: Int) {
    }
}
