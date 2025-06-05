package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext

actual class ZShaderRenderer actual constructor(ctx: ZRenderingContext, private val data: ZShaderData): ZComponentRenderer(ctx) {
    actual override fun initialize() {
        // TODO: Implement shader initialization
    }

    actual fun initialize(source: ZShaderSource) {
        // TODO: Implement shader initialization from source
    }
}
