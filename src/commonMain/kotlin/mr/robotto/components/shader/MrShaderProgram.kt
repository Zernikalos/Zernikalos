package mr.robotto.components.shader

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.components.*
import mr.robotto.math.MrMatrix4f

@Serializable
class MrShaderProgram(): MrComponent() {
    @Transient
    val program: MrProgram = MrProgram()

    @SerialName("vertexShader")
    lateinit var vertexShader: MrShader
    @SerialName("fragmentShader")
    lateinit var fragmentShader: MrShader

    private val attributes: HashMap<String, MrAttribute> = HashMap()

    private var uniforms: HashMap<String, MrUniform> = HashMap()

    constructor(vertexShaderSource: String, fragmentShaderSource: String, attributes: Map<String, IMrShaderAttribute>, uniforms: Map<String, IMrShaderUniform>) : this() {
        vertexShader = MrShader("vertex", vertexShaderSource)
        fragmentShader = MrShader("fragment", fragmentShaderSource)

        attributes.forEach { (key, attrInput) ->
            this.attributes[key] = MrAttribute(attrInput.index, attrInput.attributeName)
        }

        uniforms.forEach { (key, uniformInput) ->
            this.uniforms[key] = MrUniform(key, uniformInput.count, uniformInput.type)
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
        uniforms.values.forEach { uniform ->
            uniform.initialize(context)
            uniform.bindLocation(program)
        }
    }

    override fun render() {
        program.render()
        uniforms.values.forEach {
            val m = MrMatrix4f.Identity
            // m.translate(0f, 0f, -6f)
            m[2, 3] = -6f
            it.bindValue(m.values)
        }
    }

    private fun attachShader(program: MrProgram, shader: MrShader) {
        context.attachShader(program.programId, shader.shader)
    }

}
