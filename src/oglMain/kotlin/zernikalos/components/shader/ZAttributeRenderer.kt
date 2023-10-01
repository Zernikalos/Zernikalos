package zernikalos.components.shader

import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZAttributeRenderer: ZComponentRender<ZAttributeData> {
    actual override fun initialize(ctx: ZRenderingContext, data: ZAttributeData) {
    }

    actual fun bindLocation(ctx: ZRenderingContext, data: ZAttributeData, program: ZProgram) {
        ctx as ZGLRenderingContext

        ctx.bindAttribLocation(program.renderer.programId, data.id, data.attributeName)
    }

}