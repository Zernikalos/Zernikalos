package mr.robotto.components.shader

import kotlinx.serialization.Serializable
import mr.robotto.MrRenderingContext
import mr.robotto.components.*

interface IMrShaderAttribute {
    val index: Int
    val attributeName: String
}

@Serializable
class MrAttribute(private val index: Int, private val attributeName: String): MrComponent() {

    override fun initialize(ctx: MrRenderingContext) {
    }

    fun bindLocation(ctx: MrRenderingContext, program: MrProgram) {
        ctx.bindAttribLocation(program.programId, index, attributeName)
    }

    override fun render(ctx: MrRenderingContext) {
    }

}
