package mr.robotto.components.shader

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.GLWrap
import mr.robotto.MrTypes
import mr.robotto.components.MrComponent

interface IMrShaderUniform {
    val count: Int
    val type: MrTypes
}

@Serializable
class MrUniform(private val uniformName: String, private val count: Int, val type: MrTypes): MrComponent() {
    @Transient
    lateinit var uniformId: GLWrap

    override fun renderInitialize() {
    }

    fun bindLocation(program: MrProgram) {
        uniformId = context.getUniformLocation(program.programId, uniformName)
    }

    fun bindValue(values: FloatArray) {
        when (type) {
            MrTypes.MAT4 -> context.uniformMatrix4fv(uniformId, count, false, values)
            else -> return
        }
    }

    override fun render() {
    }

}