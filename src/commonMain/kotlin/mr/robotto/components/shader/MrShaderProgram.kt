package mr.robotto.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.components.MrComponent
import mr.robotto.components.MrComponentData
import mr.robotto.components.MrComponentRender
import mr.robotto.components.MrComponentSerializer

@Serializable(with = MrShaderProgramSerializer::class)
class MrShaderProgram: MrComponent<MrShaderProgramData, MrShaderProgramRender>() {
    override var data: MrShaderProgramData = MrShaderProgramData()
    override var renderer: MrShaderProgramRender = MrShaderProgramRender()
}

@Serializable
class MrShaderProgramData: MrComponentData() {
    @Transient
    val program: MrProgram = MrProgram()

    @SerialName("vertexShader")
    lateinit var vertexShader: MrShader
    @SerialName("fragmentShader")
    lateinit var fragmentShader: MrShader

    var attributes: Map<String, MrShaderAttribute> = HashMap()
    @Transient
    var uniforms: List<MrShaderUniform> = ArrayList()
}

class MrShaderProgramRender: MrComponentRender<MrShaderProgramData>() {
    override fun internalInitialize() {
        data.program.initialize(context)

        data.vertexShader.initialize(context)
        attachShader(data.program, data.vertexShader)

        data.fragmentShader.initialize(context)
        attachShader(data.program, data.fragmentShader)

        data.attributes.forEach { (_, value) -> value.initialize(context) }

        data.program.link()
        data.uniforms.forEach { it.initialize(context) }
    }

    override fun render() {
        data.program.render()
        data.uniforms.forEach { it.render() }
    }

    private fun attachShader(program: MrProgram, shader: MrShader) {
        context.attachShader(program.program, shader.shader)
    }

}

class MrShaderProgramSerializer: MrComponentSerializer<MrShaderProgram, MrShaderProgramData>() {
    override val deserializationStrategy: DeserializationStrategy<MrShaderProgramData> = MrShaderProgramData.serializer()

    override fun createDeserializationInstance(): MrShaderProgram {
        return MrShaderProgram()
    }

}