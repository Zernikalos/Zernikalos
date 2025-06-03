package zernikalos.components.material

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import zernikalos.components.ZComponent2
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable
actual open class ZTextureRender : ZBaseTexture {

    @Transient
    lateinit var textureHandler: GLWrap

    @JsName("init")
    actual constructor(): super() {
    }

    @JsName("initWithArgs")
    actual constructor(
        id: String,
        width: Int,
        height: Int,
        flipX: Boolean,
        flipY: Boolean,
        dataArray: ByteArray
    ): super(id, width, height, flipX, flipY, dataArray) {
    }

    override fun internalRenderInitialize(ctx: ZRenderingContext) {
        ctx as ZGLRenderingContext

        val bitmap = ZBitmap(dataArray)

        textureHandler = ctx.genTexture()

        ctx.bindTexture(textureHandler)

        ctx.texParameterMin()
        ctx.texParameterMag()

        ctx.texImage2D(bitmap)

        bitmap.dispose()
    }

    actual override fun bind(ctx: ZRenderingContext) {
        ctx as ZGLRenderingContext

        ctx.activeTexture()
        ctx.bindTexture(textureHandler)
    }

    actual override fun unbind(ctx: ZRenderingContext) {
    }

}