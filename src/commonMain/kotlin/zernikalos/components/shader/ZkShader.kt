package zernikalos.components.shader

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import zernikalos.GLWrap
import zernikalos.ShaderType
import zernikalos.ZkRenderingContext
import zernikalos.components.ZkComponent

@Serializable
class ZkShader(private val type: String, private val source: String): ZkComponent() {

    @Transient var shader: GLWrap = GLWrap()

    override fun initialize(ctx: ZkRenderingContext) {
        val type = if (this.type == "vertex") ShaderType.VERTEX_SHADER else ShaderType.FRAGMENT_SHADER
        val shad = createShader(ctx, type)
        // TODO: Take care with the cast since this breaks js
        // if (shaderId <= 0) {
        //     throw Error("Error creating shader")
        // }

        compileShader(ctx, shad, source)
        checkShader(ctx, shad)

        shader = shad
    }

    override fun render(ctx: ZkRenderingContext) {
    }

    private fun createShader(ctx: ZkRenderingContext, shaderType: ShaderType): GLWrap {
        return ctx.createShader(shaderType.value)
    }

    private fun compileShader(ctx: ZkRenderingContext, shader: GLWrap, source: String) {
        ctx.shaderSource(shader, source)
        ctx.compileShader(shader)
    }

    private fun checkShader(ctx: ZkRenderingContext, shader: GLWrap) {
        val compilerStatus = ctx.getShaderInfoLog(shader)
        val compilerError = ctx.getError()
        if (compilerStatus != "" || compilerError > 0) {
            ctx.deleteShader(shader)
            throw Error("Error compiling shader $compilerError : $compilerStatus")
        }
    }

}
