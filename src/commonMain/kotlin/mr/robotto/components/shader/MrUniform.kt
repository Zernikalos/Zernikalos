package mr.robotto.components.shader

import mr.robotto.components.MrComponent

interface IMrShaderUniform {
    val uniformName: String
}

class MrUniform(val uniformName: String): MrComponent() {
    override fun renderInitialize() {
    }

    override fun render() {
    }

}