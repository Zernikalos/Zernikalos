package zernikalos.components.mesh

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext

actual class ZBufferRenderer actual constructor(
    ctx: ZRenderingContext,
    data: ZBufferData
) : ZComponentRenderer(ctx) {
    actual override fun initialize() {
    }

    actual override fun bind() {
    }

    actual override fun unbind() {
    }
}
