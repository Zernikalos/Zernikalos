package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZAttributeSerializer::class)
class ZAttribute internal constructor(data: ZAttributeData): ZComponent<ZAttributeData, ZAttributeRenderer>(data) {

    @JsName("init")
    constructor(): this(ZAttributeData())

    @JsName("initWithArgs")
    constructor(id: Int, attributeName: String): this(ZAttributeData(id, attributeName))

    var id: Int by data::id

    var attributeName: String by data::attributeName

    override fun createRenderer(ctx: ZRenderingContext): ZAttributeRenderer {
        return ZAttributeRenderer(ctx, data)
    }
}

@Serializable
data class ZAttributeData(
    @ProtoNumber(1)
    var id: Int = -1,
    @ProtoNumber(2)
    var attributeName: String = ""
): ZComponentData()

expect class ZAttributeRenderer(ctx: ZRenderingContext, data: ZAttributeData): ZComponentRender<ZAttributeData> {
    override fun initialize()

}

class ZAttributeSerializer: ZComponentSerializer<ZAttribute, ZAttributeData>() {
    override val deserializationStrategy: DeserializationStrategy<ZAttributeData>
        get() = ZAttributeData.serializer()

    override fun createComponentInstance(data: ZAttributeData): ZAttribute {
        return ZAttribute(data)
    }

}
