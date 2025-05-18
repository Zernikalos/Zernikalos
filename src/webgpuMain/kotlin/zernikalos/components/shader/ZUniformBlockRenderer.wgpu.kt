package zernikalos.components.shader

import zernikalos.components.ZComponentRender
import zernikalos.context.ZRenderingContext

actual class ZUniformBlockRenderer actual constructor(ctx: ZRenderingContext, data: ZUniformBlockData): ZComponentRender<ZUniformBlockData>(ctx, data) {
    actual override fun initialize() {
        // TODO: Implement uniform block initialization
    }

    actual override fun bind() {
        // TODO: Implement uniform block binding
    }

    actual override fun unbind() {
        // TODO: Implement uniform block unbinding
    }
}
