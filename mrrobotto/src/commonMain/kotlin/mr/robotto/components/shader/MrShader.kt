package mr.robotto.components.shader

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.GLWrap
import mr.robotto.MrRenderingContext
import mr.robotto.ShaderType
import mr.robotto.components.*

@Serializable
class MrShader(private val type: String, private val source: String): MrComponent() {

    @Transient var shader: GLWrap = GLWrap()

    override fun initialize(ctx: MrRenderingContext) {
        val type = if (type == "vertex") ShaderType.VERTEX_SHADER else ShaderType.FRAGMENT_SHADER
        val shad = createShader(ctx, type)
        // TODO
        // if (shaderId <= 0) {
        //     throw Error("Error creating shader")
        // }

        compileShader(ctx, shad, source)
        checkShader(ctx, shad)

        shader = shad
    }

    override fun render(ctx: MrRenderingContext) {
    }

    private fun createShader(ctx: MrRenderingContext, shaderType: ShaderType): GLWrap {
        return ctx.createShader(shaderType.value)
    }

    private fun compileShader(ctx: MrRenderingContext, shader: GLWrap, source: String) {
        ctx.shaderSource(shader, source)
        ctx.compileShader(shader)
    }

    private fun checkShader(ctx: MrRenderingContext, shader: GLWrap) {
        val compilerStatus = ctx.getShaderInfoLog(shader)
        val compilerError = ctx.getError()
        if (compilerStatus != "" || compilerError > 0) {
            ctx.deleteShader(shader)
            throw Error("Error compiling shader $compilerStatus")
        }
    }

}
