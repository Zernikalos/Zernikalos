package zernikalos.components.shader

import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZAttributeRenderer actual constructor(ctx: ZRenderingContext, data: ZAttributeData): ZComponentRender<ZAttributeData>(ctx, data) {
    actual override fun initialize() {
    }

    actual fun bindLocation(program: ZProgram) {
        ctx as ZGLRenderingContext

        ctx.bindAttribLocation(program.renderer.programId, data.id, data.attributeName)
    }

}