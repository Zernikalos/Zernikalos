package zernikalos.components.material

import kotlinx.serialization.Contextual
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZRenderingContext
import zernikalos.components.*
import zernikalos.loader.ZLoaderContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ZMaterial
internal constructor(data: ZMaterialData):
    ZRefComponent<ZMaterialData, ZMaterialRenderer>(data), ZBindeable {

    @JsName("init")
    constructor(): this(ZMaterialData())

    var texture: ZTexture? by data::texture

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
data class ZMaterialDataWrapper(
    @ProtoNumber(1)
    override var refId: Int = 0,
    @ProtoNumber(2)
    override var isReference: Boolean = false,
    @ProtoNumber(100)
    override var data: ZMaterialData? = null
): ZRefComponentData<ZMaterialData>

@Serializable
@JsExport
data class ZMaterialData(
    @Contextual @ProtoNumber(1)
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

class ZMaterialSerializer(loaderContext: ZLoaderContext): ZRefComponentSerializer<ZMaterial, ZMaterialData, ZMaterialDataWrapper>(loaderContext) {
    override val deserializationStrategy: DeserializationStrategy<ZMaterialDataWrapper>
        get() = ZMaterialDataWrapper.serializer()

    override fun createComponentInstance(data: ZMaterialData): ZMaterial {
        return ZMaterial(data)
    }

}