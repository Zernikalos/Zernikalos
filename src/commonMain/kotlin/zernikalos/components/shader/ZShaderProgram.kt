package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import zernikalos.ZRenderingContext
import zernikalos.components.*

@Serializable(with = ZShaderProgramSerializer::class)
class ZShaderProgram(): ZComponent<ZShaderProgramData, ZShaderProgramRenderer>(), ZBindeable {

    val uniforms: Map<String, ZUniform>
        get() = data.uniforms

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

    override fun bind(ctx: ZRenderingContext) {
        renderer.bind(ctx, data)
    }

    override fun unbind(ctx: ZRenderingContext) {
        renderer.unbind(ctx, data)
    }

}

@Serializable
data class ZShaderProgramData(
    @SerialName("vertexShader")
    var vertexShader: ZShader,
    @SerialName("fragmentShader")
    var fragmentShader: ZShader,
    val attributes: HashMap<String, ZAttribute> = HashMap(),
    var uniforms: HashMap<String, ZUniform> = HashMap()
): ZComponentData()

class ZShaderProgramRenderer: ZComponentRender<ZShaderProgramData> {

    val program: ZProgram = ZProgram()

    override fun initialize(ctx: ZRenderingContext, data: ZShaderProgramData) {
        program.initialize(ctx)

        data.vertexShader.initialize(ctx)
        attachShader(ctx, program, data.vertexShader)

        data.fragmentShader.initialize(ctx)
        attachShader(ctx, program, data.fragmentShader)

        data.attributes.values.forEach { attr ->
            attr.initialize(ctx)
            attr.bindLocation(ctx, program)
        }

        program.link(ctx)
        data.uniforms.values.forEach { uniform ->
            uniform.initialize(ctx)
            uniform.bindLocation(ctx, program)
        }
    }

    override fun bind(ctx: ZRenderingContext, data: ZShaderProgramData) {
        program.bind(ctx)
    }

    override fun unbind(ctx: ZRenderingContext, data: ZShaderProgramData) {
        program.unbind(ctx)
    }

    private fun attachShader(ctx: ZRenderingContext, program: ZProgram, shader: ZShader) {
        ctx.attachShader(program.renderer.programId, shader.renderer.shader)
    }

}

class ZShaderProgramSerializer: ZComponentSerializer<ZShaderProgram, ZShaderProgramData, ZShaderProgramRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZShaderProgramData>
        get() = ZShaderProgramData.serializer()

    override fun createRendererComponent(): ZShaderProgramRenderer {
        return ZShaderProgramRenderer()
    }

    override fun createComponentInstance(data: ZShaderProgramData, renderer: ZShaderProgramRenderer): ZShaderProgram {
        return ZShaderProgram()
    }

}
