package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport

@JsExport
@Serializable(with = ZShaderProgramSerializer::class)
class ZShaderProgram(): ZComponent<ZShaderProgramData, ZShaderProgramRenderer>(), ZBindeable {

    // TODO: Check if these should be a val or var better in order to set up values
    val vertexShader: ZShader
        get() = data.vertexShader

    val fragmentShader: ZShader
        get() = data.fragmentShader

    val attributes: HashMap<String, ZAttribute>
        get() = data.attributes

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
    @ProtoNumber(1)
    var vertexShader: ZShader,
    @SerialName("fragmentShader")
    @ProtoNumber(2)
    var fragmentShader: ZShader,
    @ProtoNumber(3)
    val attributes: HashMap<String, ZAttribute> = HashMap(),
    @ProtoNumber(4)
    var uniforms: HashMap<String, ZUniform> = HashMap()
): ZComponentData()

expect class ZShaderProgramRenderer(): ZComponentRender<ZShaderProgramData> {

    val program: ZProgram
    override fun initialize(ctx: ZRenderingContext, data: ZShaderProgramData)
    override fun bind(ctx: ZRenderingContext, data: ZShaderProgramData)
    override fun unbind(ctx: ZRenderingContext, data: ZShaderProgramData)

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
