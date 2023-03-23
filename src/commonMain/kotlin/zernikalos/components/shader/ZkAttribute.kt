package zernikalos.components.shader

import kotlinx.serialization.Serializable
import zernikalos.ZkRenderingContext
import zernikalos.components.ZkComponent

interface IMrShaderAttribute {
    val index: Int
    val attributeName: String
}

@Serializable
class ZkAttribute(private val index: Int, private val attributeName: String): ZkComponent() {

    override fun initialize(ctx: ZkRenderingContext) {
    }

    fun bindLocation(ctx: ZkRenderingContext, program: ZkProgram) {
        ctx.bindAttribLocation(program.programId, index, attributeName)
    }

    override fun render(ctx: ZkRenderingContext) {
    }

}
