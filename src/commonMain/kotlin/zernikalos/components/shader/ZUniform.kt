package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import zernikalos.GLWrap
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
    val uniformName: String,
    val count: Int,
    val type: ZUniformType
): ZComponentData()

class ZUniformRenderer: ZComponentRender<ZUniformData> {

    lateinit var uniformId: GLWrap

    override fun initialize(ctx: ZRenderingContext, data: ZUniformData) {
    }

    fun bindLocation(ctx: ZRenderingContext, data: ZUniformData, program: ZProgram) {
        uniformId = ctx.getUniformLocation(program.renderer.programId, data.uniformName)
    }

    fun bindValue(ctx: ZRenderingContext, data: ZUniformData, values: FloatArray) {
        when (data.type) {
            ZUniformType.MAT4 -> ctx.uniformMatrix4fv(uniformId, data.count, false, values)
            else -> return
        }
    }
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