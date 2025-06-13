package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext

actual class ZShaderProgramRenderer actual constructor(
    ctx: ZRenderingContext,
    data: ZShaderProgramData
) : ZComponentRenderer(ctx) {
    actual override fun initialize() {
    }

    actual override fun bind() {
    }

    actual override fun unbind() {
    }
}
