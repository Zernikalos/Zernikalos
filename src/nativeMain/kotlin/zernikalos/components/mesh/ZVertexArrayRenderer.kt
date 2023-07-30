package zernikalos.components.mesh

import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZVertexArrayRenderer actual constructor() : ZComponentRender<ZVertexArrayData> {
    actual override fun initialize(
        ctx: ZRenderingContext,
        data: ZVertexArrayData
    ) {
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZVertexArrayData) {
    }

    actual override fun unbind(ctx: ZRenderingContext, data: ZVertexArrayData) {
    }

}