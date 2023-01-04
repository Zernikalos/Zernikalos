package mr.robotto.components.shader

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.GLWrap
import mr.robotto.ShaderType
import mr.robotto.components.*

@Serializable
class MrShader(private val type: String, private val source: String): MrComponent() {

    @Transient var shader: GLWrap = GLWrap()

    override fun renderInitialize() {
        val type = if (type == "vertex") ShaderType.VERTEX_SHADER else ShaderType.FRAGMENT_SHADER
        val shad = createShader(type)
        // TODO
        // if (shaderId <= 0) {
        //     throw Error("Error creating shader")
        // }

        compileShader(shad, source)
        checkShader(shad)

        shader = shad
    }

    override fun render() {
    }

    private fun createShader(shaderType: ShaderType): GLWrap {
        return context.createShader(shaderType.value)
    }

    private fun compileShader(shader: GLWrap, source: String) {
        context.shaderSource(shader, source)
        context.compileShader(shader)
    }

    private fun checkShader(shader: GLWrap) {
        val compilerStatus = context.getShaderInfoLog(shader)
        val compilerError = context.getError()
        if (compilerStatus != "" || compilerError > 0) {
            context.deleteShader(shader)
            throw Error("Error compiling shader $compilerStatus")
        }
    }

}
