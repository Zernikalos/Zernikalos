package zernikalos.components.shader

import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger

actual open class ZAttributeRender : ZBaseAttribute {
    actual constructor(): super()

    actual constructor(id: Int, attributeName: String): super(id, attributeName)

    actual constructor(attrId: ZAttributeId): super(attrId)

    fun bindLocation(ctx: ZRenderingContext, programId: GLWrap) {
        ctx as ZGLRenderingContext

        logger.debug("Binding shader attribute ${attributeName} to layout ${id} to program ${programId.id}")
        ctx.bindAttribLocation(programId, id, attributeName)
    }

}