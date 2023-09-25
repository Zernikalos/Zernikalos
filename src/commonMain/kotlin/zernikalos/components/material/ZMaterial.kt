package zernikalos.components.material

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport
import kotlin.js.JsName

@Serializable(with = ZMaterialSerializer::class)
@JsExport
class ZMaterial
internal constructor(data: ZMaterialData, renderer: ZMaterialRenderer):
    ZComponent<ZMaterialData, ZMaterialRenderer>(data, renderer), ZBindeable {

    @JsName("init")
    constructor(): this(ZMaterialData(), ZMaterialRenderer())

    var texture: ZTexture?
        get() = data.texture
        set(value) {
            data.texture = value
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
@JsExport
data class ZMaterialData(
    @ProtoNumber(1)
    var texture: ZTexture? = null
): ZComponentData()

class ZMaterialRenderer: ZComponentRender<ZMaterialData> {
    override fun initialize(ctx: ZRenderingContext, data: ZMaterialData) {
        data.texture?.initialize(ctx)
    }

    override fun bind(ctx: ZRenderingContext, data: ZMaterialData) {
        data.texture?.bind(ctx)
    }

    override fun unbind(ctx: ZRenderingContext, data: ZMaterialData) {
        data.texture?.unbind(ctx)
    }

}

class ZMaterialSerializer: ZComponentSerializer<ZMaterial, ZMaterialData, ZMaterialRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZMaterialData>
        get() = ZMaterialData.serializer()

    override fun createRendererComponent(): ZMaterialRenderer {
        return ZMaterialRenderer()
    }

    override fun createComponentInstance(data: ZMaterialData, renderer: ZMaterialRenderer): ZMaterial {
        return ZMaterial(data, renderer)
    }

}