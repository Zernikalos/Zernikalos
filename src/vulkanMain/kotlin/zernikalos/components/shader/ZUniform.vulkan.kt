package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext

actual class ZUniformRenderer actual constructor(
    ctx: ZRenderingContext,
    data: ZUniformData
) : ZComponentRenderer(ctx) {
    actual override fun initialize() {
    }
}
