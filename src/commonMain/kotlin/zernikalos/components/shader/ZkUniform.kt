package zernikalos.components.shader

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import zernikalos.GLWrap
import zernikalos.ZkRenderingContext
import zernikalos.ZkUniformType
import zernikalos.components.ZkComponent

interface IMrShaderUniform {
    val count: Int
    val type: ZkUniformType
}

@Serializable
class ZkUniform(private val uniformName: String, private val count: Int, val type: ZkUniformType): ZkComponent() {
    @Transient
    lateinit var uniformId: GLWrap

    override fun initialize(ctx: ZkRenderingContext) {
    }

    fun bindLocation(ctx: ZkRenderingContext, program: ZkProgram) {
        uniformId = ctx.getUniformLocation(program.programId, uniformName)
    }

    fun bindValue(ctx: ZkRenderingContext, values: FloatArray) {
        when (type) {
            ZkUniformType.MAT4 -> ctx.uniformMatrix4fv(uniformId, count, false, values)
            else -> return
        }
    }

    override fun render(ctx: ZkRenderingContext) {
    }

}