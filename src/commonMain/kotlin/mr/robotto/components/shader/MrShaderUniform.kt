package mr.robotto.components.shader

import mr.robotto.components.MrComponent
import mr.robotto.components.MrComponentData
import mr.robotto.components.MrComponentRender

class MrShaderUniform: MrComponent<MrShaderUniformData, MrShaderUniformRender>() {
    override var data: MrShaderUniformData = MrShaderUniformData()
    override var renderer: MrShaderUniformRender = MrShaderUniformRender()
}

class MrShaderUniformData: MrComponentData() {

}

class MrShaderUniformRender: MrComponentRender<MrShaderUniformData>() {
    override fun internalInitialize() {
    }

    override fun render() {
    }

}