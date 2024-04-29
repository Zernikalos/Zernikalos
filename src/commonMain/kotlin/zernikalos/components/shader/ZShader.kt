package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZRenderingContext
import zernikalos.components.*
import zernikalos.logger.logger
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
enum class ZShaderType {
    VERTEX_SHADER,
    FRAGMENT_SHADER
}

@JsExport
@Serializable(with = ZShaderSerializer::class)
class ZShader
internal constructor (data: ZShaderData): ZComponent<ZShaderData, ZShaderRenderer>(data) {

    @JsName("init")
    constructor(): this(ZShaderData())

    @JsName("initWithType")
    constructor(type: ZShaderType): this(ZShaderData(type))

    val type: ZShaderType by data::type

    @JsName("initializeWithSources")
    fun initialize(source: ZShaderSource) {
        renderer.initialize(source)
    }

    override fun createRenderer(ctx: ZRenderingContext): ZShaderRenderer {
        return ZShaderRenderer(ctx, data)
    }

}

@Serializable
data class ZShaderData(
    var type: ZShaderType = ZShaderType.VERTEX_SHADER
): ZComponentData()

expect class ZShaderRenderer(ctx: ZRenderingContext, data: ZShaderData): ZComponentRender<ZShaderData> {

    override fun initialize()

    fun initialize(source: ZShaderSource)

}

class ZShaderSerializer: ZComponentSerializer<ZShader, ZShaderData>() {
    override val deserializationStrategy: DeserializationStrategy<ZShaderData>
        get() = ZShaderData.serializer()

    override fun createComponentInstance(data: ZShaderData): ZShader {
        return ZShader(data)
    }

}
