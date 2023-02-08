package mr.robotto.components.shader

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.MrRenderingContext
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

    var uniforms: HashMap<String, MrUniform> = HashMap()

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

    override fun initialize(ctx: MrRenderingContext) {
        program.initialize(ctx)

        vertexShader.initialize(ctx)
        attachShader(ctx, program, vertexShader)

        fragmentShader.initialize(ctx)
        attachShader(ctx, program, fragmentShader)

        attributes.values.forEach { attr ->
            attr.initialize(ctx)
            attr.bindLocation(ctx, program)
        }

        program.link(ctx)
        uniforms.values.forEach { uniform ->
            uniform.initialize(ctx)
            uniform.bindLocation(ctx, program)
        }
    }

    override fun render(ctx: MrRenderingContext) {
        program.render(ctx)
    }

    private fun attachShader(ctx: MrRenderingContext, program: MrProgram, shader: MrShader) {
        ctx.attachShader(program.programId, shader.shader)
    }

}
