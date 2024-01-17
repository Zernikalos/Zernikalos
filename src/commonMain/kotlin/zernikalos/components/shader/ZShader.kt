package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZRenderingContext
import zernikalos.components.*
import zernikalos.utils.logger
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZShaderSerializer::class)
class ZShader
internal constructor (data: ZShaderData): ZComponent<ZShaderData, ZShaderRenderer>(data) {

    @JsName("init")
    constructor(): this(ZShaderData())

    @JsName("initWithArgs")
    constructor(type: String, source: String): this(ZShaderData(type, source))

    var type: String by data::type

    var source: String by data::source

    override fun internalInitialize(ctx: ZRenderingContext) {
        logger.debug("Initializing $type shader with source: \n $source")
        renderer.initialize()
    }

    override fun createRenderer(ctx: ZRenderingContext): ZShaderRenderer {
        return ZShaderRenderer(ctx, data)
    }

}

@Serializable
data class ZShaderData(
    @ProtoNumber(1)
    var type: String = "",
    @ProtoNumber(2)
    var source: String = ""
): ZComponentData()

expect class ZShaderRenderer(ctx: ZRenderingContext, data: ZShaderData): ZComponentRender<ZShaderData> {

    override fun initialize()

}

class ZShaderSerializer: ZComponentSerializer<ZShader, ZShaderData>() {
    override val deserializationStrategy: DeserializationStrategy<ZShaderData>
        get() = ZShaderData.serializer()

    override fun createComponentInstance(data: ZShaderData): ZShader {
        return ZShader(data)
    }

}
