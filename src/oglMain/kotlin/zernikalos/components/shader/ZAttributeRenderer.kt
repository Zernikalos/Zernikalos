package zernikalos.components.shader

import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender
import zernikalos.context.GLWrap
import zernikalos.utils.logger

actual class ZAttributeRenderer actual constructor(ctx: ZRenderingContext, data: ZAttributeData): ZComponentRender<ZAttributeData>(ctx, data) {
    actual override fun initialize() {
    }

    fun bindLocation(programId: GLWrap) {
        ctx as ZGLRenderingContext

        logger.debug("Binding ${data.attributeName} to ${data.id}")
        ctx.bindAttribLocation(programId, data.id, data.attributeName)
    }

}