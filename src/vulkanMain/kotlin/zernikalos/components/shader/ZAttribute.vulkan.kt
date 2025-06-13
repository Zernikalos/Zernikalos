package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext

actual class ZAttributeRenderer actual constructor(
    ctx: ZRenderingContext,
    data: ZAttributeData
) : ZComponentRenderer(ctx) {
    actual override fun initialize() {
    }
}
