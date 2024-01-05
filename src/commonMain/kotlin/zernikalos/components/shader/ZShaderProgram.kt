package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZShaderProgramSerializer::class)
class ZShaderProgram internal constructor(data: ZShaderProgramData): ZComponent<ZShaderProgramData, ZShaderProgramRenderer>(data), ZBindeable {

    @JsName("init")
    constructor(): this(ZShaderProgramData())

    var vertexShader: ZShader by data::vertexShader

    var fragmentShader: ZShader by data::fragmentShader

    val attributes: Map<String, ZAttribute> by data::attributes

    val uniforms: Map<String, ZUniform> by data::uniforms

    fun addUniform(name: String, uniform: ZUniform) {
        data.uniforms[name] = uniform
    }

    fun getUniform(name: String): ZUniform? {
        return data.uniforms[name]
    }

    fun addAttribute(name: String, attribute: ZAttribute) {
        data.attributes[name] = attribute
    }

    override fun internalInitialize(ctx: ZRenderingContext) {
        renderer.initialize()
    }

    override fun createRenderer(ctx: ZRenderingContext): ZShaderProgramRenderer {
        return ZShaderProgramRenderer(ctx, data)
    }

    override fun bind() {
        renderer.bind()
    }

    override fun unbind() {
        renderer.unbind()
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

expect class ZShaderProgramRenderer(ctx: ZRenderingContext, data: ZShaderProgramData): ZComponentRender<ZShaderProgramData> {

    val program: ZProgram
    override fun initialize()
    override fun bind()
    override fun unbind()

}

class ZShaderProgramSerializer: ZComponentSerializer<ZShaderProgram, ZShaderProgramData>() {
    override val deserializationStrategy: DeserializationStrategy<ZShaderProgramData>
        get() = ZShaderProgramData.serializer()

    override fun createComponentInstance(data: ZShaderProgramData): ZShaderProgram {
        return ZShaderProgram(data)
    }

}
