package zernikalos.components.shader

import zernikalos.components.ZComponentRender
import zernikalos.context.ZRenderingContext

actual class ZAttributeRenderer actual constructor(ctx: ZRenderingContext, data: ZAttributeData): ZComponentRender<ZAttributeData>(ctx, data) {
    actual override fun initialize() {
        // TODO: Implement attribute initialization
    }
}
