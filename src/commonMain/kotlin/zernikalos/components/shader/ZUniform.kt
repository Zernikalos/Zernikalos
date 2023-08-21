package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.ZRenderingContext
import zernikalos.components.*


@Serializable(with = ZUniformSerializer::class)
class ZUniform: ZComponent<ZUniformData, ZUniformRenderer>() {

    val uniformName: String
        get() = data.uniformName

    val count: Int
        get() = data.count

    val dataType: ZDataType
        get() = data.dataType

    override fun initialize(ctx: ZRenderingContext) {
    }

    fun bindLocation(ctx: ZRenderingContext, program: ZProgram) {
        renderer.bindLocation(ctx, data, program)
    }

    fun bindValue(ctx: ZRenderingContext, values: FloatArray) {
        renderer.bindValue(ctx, data, values)
    }

}

@Serializable
data class ZUniformData(
    @ProtoNumber(1)
    val uniformName: String,
    @ProtoNumber(2)
    val count: Int,
    @ProtoNumber(3)
    val dataType: ZDataType
): ZComponentData()

expect class ZUniformRenderer(): ZComponentRender<ZUniformData> {

    override fun initialize(ctx: ZRenderingContext, data: ZUniformData)

    fun bindLocation(ctx: ZRenderingContext, data: ZUniformData, program: ZProgram)

    fun bindValue(ctx: ZRenderingContext, data: ZUniformData, values: FloatArray)
}

class ZUniformSerializer: ZComponentSerializer<ZUniform, ZUniformData, ZUniformRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZUniformData>
        get() = ZUniformData.serializer()

    override fun createRendererComponent(): ZUniformRenderer {
        return ZUniformRenderer()
    }

    override fun createComponentInstance(data: ZUniformData, renderer: ZUniformRenderer): ZUniform {
        return ZUniform()
    }

}