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
class ZAttribute internal constructor(data: ZAttributeData, renderer: ZAttributeRenderer): ZComponent<ZAttributeData, ZAttributeRenderer>(data, renderer) {

    @JsName("init")
    constructor(): this(ZAttributeData(), ZAttributeRenderer())

    @JsName("initWithArgs")
    constructor(id: Int, attributeName: String): this(ZAttributeData(id, attributeName), ZAttributeRenderer())

    var id: Int
        get() = data.id
        set(value) {
            data.id = value
        }

    var attributeName: String
        get() = data.attributeName
        set(value) {
            data.attributeName = value
        }

    override fun initialize(ctx: ZRenderingContext) {
    }

    fun bindLocation(ctx: ZRenderingContext, program: ZProgram) {
        renderer.bindLocation(ctx, data, program)
    }
}

@Serializable
data class ZAttributeData(
    @ProtoNumber(1)
    var id: Int = -1,
    @ProtoNumber(2)
    var attributeName: String = ""
): ZComponentData()

expect class ZAttributeRenderer(): ZComponentRender<ZAttributeData> {
    override fun initialize(ctx: ZRenderingContext, data: ZAttributeData)

    fun bindLocation(ctx: ZRenderingContext, data: ZAttributeData, program: ZProgram)

}

class ZAttributeSerializer: ZComponentSerializer<ZAttribute, ZAttributeData, ZAttributeRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZAttributeData>
        get() = ZAttributeData.serializer()

    override fun createRendererComponent(): ZAttributeRenderer {
        return ZAttributeRenderer()
    }

    override fun createComponentInstance(data: ZAttributeData, renderer: ZAttributeRenderer): ZAttribute {
        return ZAttribute(data, renderer)
    }

}
