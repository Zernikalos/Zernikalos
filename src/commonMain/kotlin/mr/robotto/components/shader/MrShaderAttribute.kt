package mr.robotto.components.shader

import kotlinx.serialization.Serializable
import mr.robotto.components.*

@Serializable
class MrShaderAttribute(val index: Int, val attributeName: String): MrComponent() {
    override fun renderInitialize() {
    }

    fun bindLocation(program: MrProgram) {
        context.bindAttribLocation(program.program, index, attributeName)
    }

    override fun render() {
    }

}
