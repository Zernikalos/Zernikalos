package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZShaderSerializer::class)
class ZShader
internal constructor (data: ZShaderData, renderer: ZShaderRenderer): ZComponent<ZShaderData, ZShaderRenderer>(data, renderer) {

    @JsName("init")
    constructor(): this(ZShaderData(), ZShaderRenderer())

    @JsName("initWithArgs")
    constructor(type: String, source: String): this(ZShaderData(type, source), ZShaderRenderer())


    var type: String
        get() = data.type
        set(value) {
            data.type = value
        }

    var source: String
        get() = data.source
        set(value) {
            data.source = value
        }

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

}

@Serializable
data class ZShaderData(
    @ProtoNumber(1)
    var type: String = "",
    @ProtoNumber(2)
    var source: String = ""
): ZComponentData()

expect class ZShaderRenderer(): ZComponentRender<ZShaderData> {

    override fun initialize(ctx: ZRenderingContext, data: ZShaderData)

}

class ZShaderSerializer: ZComponentSerializer<ZShader, ZShaderData, ZShaderRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZShaderData>
        get() = ZShaderData.serializer()

    override fun createRendererComponent(): ZShaderRenderer {
        return ZShaderRenderer()
    }

    override fun createComponentInstance(data: ZShaderData, renderer: ZShaderRenderer): ZShader {
        return ZShader(data, renderer)
    }

}
