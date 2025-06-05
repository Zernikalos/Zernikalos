package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext

actual class ZShaderProgramRenderer actual constructor(ctx: ZRenderingContext, private val data: ZShaderProgramData): ZComponentRenderer(ctx) {
    actual override fun initialize() {
        // TODO: Implement shader program initialization
    }

    actual override fun bind() {
        // TODO: Implement shader program binding
    }

    actual override fun unbind() {
        // TODO: Implement shader program unbinding
    }
}
