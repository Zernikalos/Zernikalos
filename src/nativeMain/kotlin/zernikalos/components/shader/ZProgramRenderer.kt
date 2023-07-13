package zernikalos.components.shader

import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZProgramRenderer actual constructor() : ZComponentRender<ZProgramData> {
    actual override fun initialize(ctx: ZRenderingContext, data: ZProgramData) {
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZProgramData) {
    }

    actual fun link(ctx: ZRenderingContext) {
    }

}