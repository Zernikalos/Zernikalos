package zernikalos.components.shader

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import zernikalos.GLWrap
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponent

interface IZShaderUniform {
    val count: Int
    val type: ZUniformType
}

@Serializable
class ZUniform(private val uniformName: String, private val count: Int, val type: ZUniformType): ZComponent() {
    @Transient
    lateinit var uniformId: GLWrap

    override fun initialize(ctx: ZRenderingContext) {
    }

    fun bindLocation(ctx: ZRenderingContext, program: ZProgram) {
        uniformId = ctx.getUniformLocation(program.programId, uniformName)
    }

    fun bindValue(ctx: ZRenderingContext, values: FloatArray) {
        when (type) {
            ZUniformType.MAT4 -> ctx.uniformMatrix4fv(uniformId, count, false, values)
            else -> return
        }
    }

    override fun render(ctx: ZRenderingContext) {
    }

}

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