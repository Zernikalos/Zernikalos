package zernikalos.components.shader

import zernikalos.GLWrap
import zernikalos.ShaderType
import zernikalos.ZGLRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZShaderRenderer: ZComponentRender<ZShaderData> {

    var shader: GLWrap = GLWrap()

    actual override fun initialize(ctx: ZRenderingContext, data: ZShaderData) {
        val type = if (data.type == "vertex") ShaderType.VERTEX_SHADER else ShaderType.FRAGMENT_SHADER
        val shad = createShader(ctx, type)
        // TODO: Take care with the cast since this breaks js
        // if (shaderId <= 0) {
        //     throw Error("Error creating shader")
        // }

        compileShader(ctx, shad, data.source)
        checkShader(ctx, shad)

        shader = shad
    }

    private fun createShader(ctx: ZRenderingContext, shaderType: ShaderType): GLWrap {
        ctx as ZGLRenderingContext

        return ctx.createShader(shaderType.value)
    }

    private fun compileShader(ctx: ZRenderingContext, shader: GLWrap, source: String) {
        ctx as ZGLRenderingContext

        ctx.shaderSource(shader, source)
        ctx.compileShader(shader)
    }

    private fun checkShader(ctx: ZRenderingContext, shader: GLWrap) {
        ctx as ZGLRenderingContext

        val compilerStatus = ctx.getShaderInfoLog(shader)
        val compilerError = ctx.getError()
        if (compilerStatus != "" || compilerError > 0) {
            ctx.deleteShader(shader)
            throw Error("Error compiling shader $compilerError : $compilerStatus")
        }
    }

}