package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.GLWrap
import zernikalos.ShaderType
import zernikalos.ZRenderingContext
import zernikalos.components.*

@Serializable(with = ZShaderSerializer::class)
class ZShader(): ZComponent<ZShaderData, ZShaderRenderer>() {

    // TODO: Improve typing
    val type: String
        get() = data.type

    val source: String
        get() = data.source

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

}

@Serializable
data class ZShaderData(
    @ProtoNumber(1)
    val type: String,
    @ProtoNumber(2)
    val source: String
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
        return ZShader()
    }

}
