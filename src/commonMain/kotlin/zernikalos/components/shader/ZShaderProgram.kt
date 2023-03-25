package zernikalos.components.shader

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponent

@Serializable
class ZShaderProgram(): ZComponent() {
    @Transient
    val program: ZProgram = ZProgram()

    @SerialName("vertexShader")
    lateinit var vertexShader: ZShader
    @SerialName("fragmentShader")
    lateinit var fragmentShader: ZShader

    private val attributes: HashMap<String, ZAttribute> = HashMap()

    var uniforms: HashMap<String, ZUniform> = HashMap()

    constructor(vertexShaderSource: String, fragmentShaderSource: String, attributes: Map<String, IZShaderAttribute>, uniforms: Map<String, IZShaderUniform>) : this() {
        vertexShader = ZShader("vertex", vertexShaderSource)
        fragmentShader = ZShader("fragment", fragmentShaderSource)

        attributes.forEach { (key, attrInput) ->
            this.attributes[key] = ZAttribute(attrInput.index, attrInput.attributeName)
        }

        uniforms.forEach { (key, uniformInput) ->
            this.uniforms[key] = ZUniform(key, uniformInput.count, uniformInput.type)
        }
    }

    override fun initialize(ctx: ZRenderingContext) {
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

    override fun render(ctx: ZRenderingContext) {
        program.render(ctx)
    }

    private fun attachShader(ctx: ZRenderingContext, program: ZProgram, shader: ZShader) {
        ctx.attachShader(program.programId, shader.shader)
    }

}
