package zernikalos.components.shader

import zernikalos.components.ZComponentRender
import zernikalos.context.ZRenderingContext

actual class ZShaderProgramRenderer actual constructor(ctx: ZRenderingContext, data: ZShaderProgramData): ZComponentRender<ZShaderProgramData>(ctx, data) {
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
