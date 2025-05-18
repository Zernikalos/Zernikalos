package zernikalos.components.shader

import zernikalos.components.ZComponentRender
import zernikalos.context.ZRenderingContext

actual class ZShaderRenderer actual constructor(ctx: ZRenderingContext, data: ZShaderData): ZComponentRender<ZShaderData>(ctx, data) {
    actual override fun initialize() {
        // TODO: Implement shader initialization
    }

    actual fun initialize(source: ZShaderSource) {
        // TODO: Implement shader initialization from source
    }
}
