package zernikalos.components.material

import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZTextureRenderer actual constructor(ctx: ZRenderingContext, data: ZTextureData) : ZComponentRender<ZTextureData>(ctx, data) {
    actual override fun initialize() {
    }

}