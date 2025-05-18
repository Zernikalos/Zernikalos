package zernikalos.components.shader

import zernikalos.components.ZComponentRender
import zernikalos.context.ZRenderingContext

actual class ZUniformRenderer actual constructor(ctx: ZRenderingContext, data: ZUniformData): ZComponentRender<ZUniformData>(ctx, data) {
    actual override fun initialize() {
        // TODO: Implement uniform initialization
    }
}
