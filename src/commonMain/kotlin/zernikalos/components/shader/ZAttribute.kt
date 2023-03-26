package zernikalos.components.shader

import kotlinx.serialization.Serializable
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponent

interface IZShaderAttribute {
    val index: Int
    val attributeName: String
}

@Serializable
class ZAttribute(private val index: Int, private val attributeName: String): ZComponent() {

    override fun initialize(ctx: ZRenderingContext) {
    }

    fun bindLocation(ctx: ZRenderingContext, program: ZProgram) {
        ctx.bindAttribLocation(program.programId, index, attributeName)
    }

    override fun render(ctx: ZRenderingContext) {
    }

}
