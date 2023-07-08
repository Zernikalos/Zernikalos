package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZRenderingContext
import zernikalos.components.*

@Serializable(with = ZAttributeSerializer::class)
class ZAttribute: ZComponent<ZAttributeData, ZAttributeRenderer>() {

    val id: Int
        get() = data.id

    val attributeName: String
        get() = data.attributeName

    override fun initialize(ctx: ZRenderingContext) {
    }

    fun bindLocation(ctx: ZRenderingContext, program: ZProgram) {
        renderer.bindLocation(ctx, data, program)
    }
}

@Serializable
data class ZAttributeData(
    @ProtoNumber(1)
    val id: Int,
    @ProtoNumber(2)
    val attributeName: String
): ZComponentData()

class ZAttributeRenderer: ZComponentRender<ZAttributeData> {
    override fun initialize(ctx: ZRenderingContext, data: ZAttributeData) {
    }

    fun bindLocation(ctx: ZRenderingContext,data: ZAttributeData, program: ZProgram) {
        ctx.bindAttribLocation(program.renderer.programId, data.id, data.attributeName)
    }

}

class ZAttributeSerializer: ZComponentSerializer<ZAttribute, ZAttributeData, ZAttributeRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZAttributeData>
        get() = ZAttributeData.serializer()

    override fun createRendererComponent(): ZAttributeRenderer {
        return ZAttributeRenderer()
    }

    override fun createComponentInstance(data: ZAttributeData, renderer: ZAttributeRenderer): ZAttribute {
        return ZAttribute()
    }

}
