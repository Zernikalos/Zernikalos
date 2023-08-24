package zernikalos.components.material

import zernikalos.GLWrap
import zernikalos.ZGLRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponentRender

actual class ZTextureRenderer actual constructor() : ZComponentRender<ZTextureData> {

    lateinit var textureHandler: GLWrap

    override fun initialize(ctx: ZRenderingContext, data: ZTextureData) {
        ctx as ZGLRenderingContext

        textureHandler = ctx.genTexture()

        ctx.bindTexture(textureHandler)

        ctx.texParameterMin()
        ctx.texParameterMag()

        ctx.texImage2D(data.bitmap)

        data.bitmap.dispose()
    }

    override fun bind(ctx: ZRenderingContext, data: ZTextureData) {
        ctx as ZGLRenderingContext

        ctx.activeTexture()
        ctx.bindTexture(textureHandler)
    }

    override fun unbind(ctx: ZRenderingContext, data: ZTextureData) {
    }

}