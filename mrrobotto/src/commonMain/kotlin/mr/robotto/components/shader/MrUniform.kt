package mr.robotto.components.shader

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.GLWrap
import mr.robotto.MrRenderingContext
import mr.robotto.MrUniformType
import mr.robotto.components.MrComponent

interface IMrShaderUniform {
    val count: Int
    val type: MrUniformType
}

@Serializable
class MrUniform(private val uniformName: String, private val count: Int, val type: MrUniformType): MrComponent() {
    @Transient
    lateinit var uniformId: GLWrap

    override fun initialize(ctx: MrRenderingContext) {
    }

    fun bindLocation(ctx: MrRenderingContext, program: MrProgram) {
        uniformId = ctx.getUniformLocation(program.programId, uniformName)
    }

    fun bindValue(ctx: MrRenderingContext, values: FloatArray) {
        when (type) {
            MrUniformType.MAT4 -> ctx.uniformMatrix4fv(uniformId, count, false, values)
            else -> return
        }
    }

    override fun render(ctx: MrRenderingContext) {
    }

}