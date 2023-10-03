package zernikalos.components.shader

import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZProgramRenderer actual constructor(ctx: ZRenderingContext, data: ZProgramData) : ZComponentRender<ZProgramData>(ctx, data) {
    actual override fun initialize() {
    }

    actual override fun bind() {
    }

    actual fun link() {
    }

}