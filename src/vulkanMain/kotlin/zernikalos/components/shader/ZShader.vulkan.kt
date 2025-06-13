package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext

actual class ZShaderRenderer actual constructor(
    ctx: ZRenderingContext,
    data: ZShaderData
) : ZComponentRenderer(ctx) {
    actual override fun initialize() {
    }

    actual fun initialize(source: ZShaderSource) {
    }
}
