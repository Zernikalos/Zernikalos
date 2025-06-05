package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext

actual class ZUniformBlockRenderer actual constructor(ctx: ZRenderingContext, private val data: ZUniformBlockData): ZComponentRenderer(ctx) {
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
