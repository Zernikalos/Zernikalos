package zernikalos.components.shader

import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZShaderRenderer actual constructor(ctx: ZRenderingContext, data: ZShaderData) : ZComponentRender<ZShaderData>(ctx, data) {
    actual override fun initialize() {
    }

}