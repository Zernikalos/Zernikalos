package zernikalos.components.material

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport
import kotlin.js.JsName

@Serializable(with = ZMaterialSerializer::class)
@JsExport
class ZMaterial
internal constructor(data: ZMaterialData):
    ZComponent<ZMaterialData, ZMaterialRenderer>(data), ZBindeable {

    @JsName("init")
    constructor(): this(ZMaterialData())

    var texture: ZTexture? by data::texture

    override fun internalInitialize(ctx: ZRenderingContext) {
        renderer.initialize()
    }

    override fun createRenderer(ctx: ZRenderingContext): ZMaterialRenderer {
        return ZMaterialRenderer(ctx, data)
    }

    override fun bind() {
        renderer.bind()
    }

    override fun unbind() {
        renderer.unbind()
    }
}

@Serializable
@JsExport
data class ZMaterialData(
    @ProtoNumber(1)
    var texture: ZTexture? = null
): ZComponentData()

class ZMaterialRenderer(ctx: ZRenderingContext, data: ZMaterialData): ZComponentRender<ZMaterialData>(ctx, data) {
    override fun initialize() {
        data.texture?.initialize(ctx)
    }

    override fun bind() {
        data.texture?.bind()
    }

    override fun unbind() {
        data.texture?.unbind()
    }

}

class ZMaterialSerializer: ZComponentSerializer<ZMaterial, ZMaterialData, ZMaterialRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZMaterialData>
        get() = ZMaterialData.serializer()

    override fun createComponentInstance(data: ZMaterialData): ZMaterial {
        return ZMaterial(data)
    }

}