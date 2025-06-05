package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext

actual class ZUniformRenderer actual constructor(ctx: ZRenderingContext, private val data: ZUniformData): ZComponentRenderer(ctx) {
    actual override fun initialize() {
        // TODO: Implement uniform initialization
    }
}
