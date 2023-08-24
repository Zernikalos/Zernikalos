package zernikalos.components.material

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport

@Serializable(with = ZTextureSerializer::class)
@JsExport
class ZTexture: ZComponent<ZTextureData, ZTextureRenderer>(), ZBindeable {
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
data class ZTextureData(
    @ProtoNumber(1)
    val id: String,
    @ProtoNumber(2)
    val dataArray: ByteArray,
): ZComponentData() {

    @Transient
    var bitmap: ZBitmap = ZBitmap(dataArray)

}

expect class ZTextureRenderer(): ZComponentRender<ZTextureData> {

}

class ZTextureSerializer: ZComponentSerializer<ZTexture, ZTextureData, ZTextureRenderer>() {

    override val deserializationStrategy: DeserializationStrategy<ZTextureData> = ZTextureData.serializer()

    override fun createRendererComponent(): ZTextureRenderer {
        return ZTextureRenderer()
    }

    override fun createComponentInstance(data: ZTextureData, renderer: ZTextureRenderer): ZTexture {
        return ZTexture()
    }

}