package zernikalos.components.shader

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import zernikalos.ZkRenderingContext
import zernikalos.components.ZkComponent

@Serializable
class ZkShaderProgram(): ZkComponent() {
    @Transient
    val program: ZkProgram = ZkProgram()

    @SerialName("vertexShader")
    lateinit var vertexShader: ZkShader
    @SerialName("fragmentShader")
    lateinit var fragmentShader: ZkShader

    private val attributes: HashMap<String, ZkAttribute> = HashMap()

    var uniforms: HashMap<String, ZkUniform> = HashMap()

    constructor(vertexShaderSource: String, fragmentShaderSource: String, attributes: Map<String, IMrShaderAttribute>, uniforms: Map<String, IMrShaderUniform>) : this() {
        vertexShader = ZkShader("vertex", vertexShaderSource)
        fragmentShader = ZkShader("fragment", fragmentShaderSource)

        attributes.forEach { (key, attrInput) ->
            this.attributes[key] = ZkAttribute(attrInput.index, attrInput.attributeName)
        }

        uniforms.forEach { (key, uniformInput) ->
            this.uniforms[key] = ZkUniform(key, uniformInput.count, uniformInput.type)
        }
    }

    override fun initialize(ctx: ZkRenderingContext) {
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

    override fun render(ctx: ZkRenderingContext) {
        program.render(ctx)
    }

    private fun attachShader(ctx: ZkRenderingContext, program: ZkProgram, shader: ZkShader) {
        ctx.attachShader(program.programId, shader.shader)
    }

}
