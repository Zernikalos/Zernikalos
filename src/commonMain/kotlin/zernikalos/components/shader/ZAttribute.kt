package zernikalos.components.shader

import kotlinx.serialization.Serializable
import zernikalos.ZRenderingContext
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponent

interface IZShaderAttribute {
    val id: Int
    val attributeName: String
}

@Serializable
class ZAttribute(private val id: Int, private val attributeName: String): ZComponent() {

    override fun initialize(ctx: ZRenderingContext) {
    }

    fun bindLocation(ctx: ZRenderingContext, program: ZProgram) {
        ctx.bindAttribLocation(program.programId, id, attributeName)
    }


}
