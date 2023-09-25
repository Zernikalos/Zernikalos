package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZShaderProgramSerializer::class)
class ZShaderProgram internal constructor(data: ZShaderProgramData, renderer: ZShaderProgramRenderer): ZComponent<ZShaderProgramData, ZShaderProgramRenderer>(data, renderer), ZBindeable {

    @JsName("init")
    constructor(): this(ZShaderProgramData(), ZShaderProgramRenderer())

    var vertexShader: ZShader
        get() = data.vertexShader
        set(value) {
            data.vertexShader = value
        }

    var fragmentShader: ZShader
        get() = data.fragmentShader
        set(value) {
            data.fragmentShader = value
        }

    val attributes: Map<String, ZAttribute>
        get() = data.attributes

    val uniforms: Map<String, ZUniform>
        get() = data.uniforms

    fun addUniform(name: String, uniform: ZUniform) {
        data.uniforms[name] = uniform
    }

    fun addAttribute(name: String, attribute: ZAttribute) {
        data.attributes[name] = attribute
    }

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
    @ProtoNumber(1)
    var vertexShader: ZShader = ZShader(),
    @ProtoNumber(2)
    var fragmentShader: ZShader = ZShader(),
    @ProtoNumber(3)
    val attributes: LinkedHashMap<String, ZAttribute> = LinkedHashMap(),
    @ProtoNumber(4)
    var uniforms: LinkedHashMap<String, ZUniform> = LinkedHashMap()
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
