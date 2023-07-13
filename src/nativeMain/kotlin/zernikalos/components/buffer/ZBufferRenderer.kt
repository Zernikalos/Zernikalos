package zernikalos.components.buffer

import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZBufferRenderer actual constructor() : ZComponentRender<ZBufferData> {
    actual override fun initialize(ctx: ZRenderingContext, data: ZBufferData) {
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZBufferData) {
    }

    actual override fun unbind(ctx: ZRenderingContext, data: ZBufferData) {
    }

}