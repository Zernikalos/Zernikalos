package zernikalos.components.material

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport
import kotlin.js.JsName

@Serializable(with = ZTextureSerializer::class)
@JsExport
class ZTexture internal constructor(data: ZTextureData, renderer: ZTextureRenderer): ZComponent<ZTextureData, ZTextureRenderer>(data, renderer), ZBindeable {

    @JsName("init")
    constructor(): this(ZTextureData(), ZTextureRenderer())

    @JsName("initWithArgs")
    constructor(id: String, dataArray: ByteArray): this(ZTextureData(id, dataArray), ZTextureRenderer())

    var id: String
        get() = data.id
        set(value) {
            data.id = value
        }

    var dataArray: ByteArray
        get() = data.dataArray
        set(value) {
            data.dataArray = value
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
data class ZTextureData(
    @ProtoNumber(1)
    var id: String = "",
    @ProtoNumber(2)
    var dataArray: ByteArray = byteArrayOf(),
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
        return ZTexture(data, renderer)
    }

}