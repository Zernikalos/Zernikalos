package mr.robotto.components.shader

import kotlinx.serialization.Serializable
import mr.robotto.components.*

interface IMrShaderAttribute {
    val index: Int
    val attributeName: String
}

@Serializable
class MrAttribute(val index: Int, val attributeName: String): MrComponent() {

    override fun renderInitialize() {
    }

    fun bindLocation(program: MrProgram) {
        context.bindAttribLocation(program.programId, index, attributeName)
    }

    override fun render() {
    }

}
