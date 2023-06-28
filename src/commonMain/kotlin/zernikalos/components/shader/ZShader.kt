package zernikalos.components.shader

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import zernikalos.GLWrap
import zernikalos.ShaderType
import zernikalos.ZRenderingContext
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponent

@Serializable
class ZShader(private val type: String, private val source: String): ZComponent() {

    @Transient var shader: GLWrap = GLWrap()

    override fun initialize(ctx: ZRenderingContext) {
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

    private fun createShader(ctx: ZRenderingContext, shaderType: ShaderType): GLWrap {
        return ctx.createShader(shaderType.value)
    }

    private fun compileShader(ctx: ZRenderingContext, shader: GLWrap, source: String) {
        ctx.shaderSource(shader, source)
        ctx.compileShader(shader)
    }

    private fun checkShader(ctx: ZRenderingContext, shader: GLWrap) {
        val compilerStatus = ctx.getShaderInfoLog(shader)
        val compilerError = ctx.getError()
        if (compilerStatus != "" || compilerError > 0) {
            ctx.deleteShader(shader)
            throw Error("Error compiling shader $compilerError : $compilerStatus")
        }
    }

}
