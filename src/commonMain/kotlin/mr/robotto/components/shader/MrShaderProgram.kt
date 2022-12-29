package mr.robotto.components.shader

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.components.*

@Serializable
class MrShaderProgram: MrComponent {
    @Transient
    val program: MrProgram = MrProgram()

    @SerialName("vertexShader")
    lateinit var vertexShader: MrShader
    @SerialName("fragmentShader")
    lateinit var fragmentShader: MrShader

    val attributes: HashMap<String, MrAttribute> = HashMap()

    @Transient
    var uniforms: HashMap<String, MrUniform> = HashMap()

    constructor(vertexShaderSource: String, fragmentShaderSource: String, attributes: Map<String, IMrShaderAttribute>, uniforms: Map<String, IMrShaderUniform>) {
        vertexShader = MrShader("vertex", vertexShaderSource)
        fragmentShader = MrShader("fragment", fragmentShaderSource)

        attributes.forEach { (key, attrInput) ->
            val name = attrInput.attributeName
            this.attributes[name] = MrAttribute(attrInput.index, attrInput.attributeName)
        }

        uniforms.forEach { (key, uniformInput) ->
            val name = uniformInput.uniformName
            this.uniforms[name] = MrUniform(name)
        }
    }

    override fun renderInitialize() {
        program.initialize(context)

        vertexShader.initialize(context)
        attachShader(program, vertexShader)

        fragmentShader.initialize(context)
        attachShader(program, fragmentShader)

        attributes.values.forEach { attr ->
            attr.initialize(context)
            attr.bindLocation(program)
        }

        program.link()
        uniforms.values.forEach { it.initialize(context) }
    }

    override fun render() {
        program.render()
    }

    private fun attachShader(program: MrProgram, shader: MrShader) {
        context.attachShader(program.program, shader.shader)
    }

}
