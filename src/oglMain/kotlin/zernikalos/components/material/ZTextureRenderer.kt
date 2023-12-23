package zernikalos.components.material

import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZTextureRenderer actual constructor(ctx: ZRenderingContext, data: ZTextureData) : ZComponentRender<ZTextureData>(ctx, data) {

    lateinit var textureHandler: GLWrap

    actual override fun initialize() {
        ctx as ZGLRenderingContext

        textureHandler = ctx.genTexture()

        ctx.bindTexture(textureHandler)

        ctx.texParameterMin()
        ctx.texParameterMag()

        ctx.texImage2D(data.bitmap)

        data.bitmap.dispose()
    }

    override fun bind() {
        ctx as ZGLRenderingContext

        ctx.activeTexture()
        ctx.bindTexture(textureHandler)
    }

    override fun unbind() {
    }

}