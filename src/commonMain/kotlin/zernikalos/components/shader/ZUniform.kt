package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZRenderingContext
import zernikalos.components.*

@Serializable
enum class ZUniformType {
    @SerialName("scalar")
    SCALAR,

    @SerialName("vec2")
    VEC2,
    @SerialName("vec3")
    VEC3,
    @SerialName("vec4")
    VEC4,

    @SerialName("mat2")
    MAT2,
    @SerialName("mat3")
    MAT3,
    @SerialName("mat4")
    MAT4
}

@Serializable(with = ZUniformSerializer::class)
class ZUniform: ZComponent<ZUniformData, ZUniformRenderer>() {

    val uniformName: String
        get() = data.uniformName

    val count: Int
        get() = data.count

    val type: ZUniformType
        get() = data.type

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
    val type: ZUniformType
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