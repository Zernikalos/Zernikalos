package zernikalos.components.material

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport
import kotlin.js.JsName

@Serializable(with = ZTextureSerializer::class)
@JsExport
class ZTexture internal constructor(data: ZTextureData): ZComponent<ZTextureData, ZTextureRenderer>(data), ZBindeable {

    @JsName("init")
    constructor(): this(ZTextureData())

    @JsName("initWithArgs")
    constructor(id: String, dataArray: ByteArray): this(ZTextureData(id, dataArray))

    var id: String by data::id

    var dataArray: ByteArray by data::dataArray

    override fun internalInitialize(ctx: ZRenderingContext) {
        renderer.initialize()
    }

    override fun createRenderer(ctx: ZRenderingContext): ZTextureRenderer {
        return ZTextureRenderer(ctx, data)
    }

    override fun bind() {
        renderer.bind()
    }

    override fun unbind() {
        renderer.unbind()
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

expect class ZTextureRenderer(ctx: ZRenderingContext, data: ZTextureData): ZComponentRender<ZTextureData> {

}

class ZTextureSerializer: ZComponentSerializer<ZTexture, ZTextureData, ZTextureRenderer>() {

    override val deserializationStrategy: DeserializationStrategy<ZTextureData> = ZTextureData.serializer()

    override fun createComponentInstance(data: ZTextureData): ZTexture {
        return ZTexture(data)
    }

}