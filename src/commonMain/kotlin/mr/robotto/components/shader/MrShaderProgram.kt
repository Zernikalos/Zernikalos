package mr.robotto.components.shader

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.components.*

@Serializable
class MrShaderProgram: MrComponent() {
    @Transient
    val program: MrProgram = MrProgram()

    @SerialName("vertexShader")
    lateinit var vertexShader: MrShader
    @SerialName("fragmentShader")
    lateinit var fragmentShader: MrShader

    var attributes: Map<String, MrShaderAttribute> = HashMap()

    @Transient
    var uniforms: List<MrShaderUniform> = ArrayList()

    override fun renderInitialize() {
        program.initialize(context)

        vertexShader.initialize(context)
        attachShader(program, vertexShader)

        fragmentShader.initialize(context)
        attachShader(program, fragmentShader)

        attributes.forEach { (_, value) -> value.initialize(context) }

        program.link()
        uniforms.forEach { it.initialize(context) }
    }

    override fun render() {
    }

    private fun attachShader(program: MrProgram, shader: MrShader) {
        context.attachShader(program.program, shader.shader)
    }

}
