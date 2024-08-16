package zernikalos.components.material

import zernikalos.components.ZComponentRender
import zernikalos.context.ZRenderingContext

/**
 * @suppress
 */
actual class ZTextureRenderer actual constructor(
    ctx: ZRenderingContext,
    data: ZTextureData
) : ZComponentRender<ZTextureData>(ctx, data) {
    actual override fun initialize() {
    }

    actual override fun render() {
    }

}