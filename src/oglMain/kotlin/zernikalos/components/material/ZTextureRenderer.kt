package zernikalos.components.material

import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender
import zernikalos.logger.logger

actual class ZTextureRenderer actual constructor(ctx: ZRenderingContext, data: ZTextureData) : ZComponentRender<ZTextureData>(ctx, data) {

    lateinit var textureHandler: GLWrap

    actual override fun initialize() {
        ctx as ZGLRenderingContext

        //logger.debug("Initializing Texture with id ${data.refId}")

        val bitmap = ZBitmap(data.dataArray!!)

        textureHandler = ctx.genTexture()

        ctx.bindTexture(textureHandler)

        ctx.texParameterMin()
        ctx.texParameterMag()

        ctx.texImage2D(bitmap)

        bitmap.dispose()
    }

    override fun bind() {
        ctx as ZGLRenderingContext

        ctx.activeTexture()
        ctx.bindTexture(textureHandler)
    }

    override fun unbind() {
    }

}