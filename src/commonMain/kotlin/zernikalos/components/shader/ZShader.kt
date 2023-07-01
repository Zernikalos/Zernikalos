package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import zernikalos.GLWrap
import zernikalos.ShaderType
import zernikalos.ZRenderingContext
import zernikalos.components.*

@Serializable(with = ZShaderSerializer::class)
class ZShader(): ZComponent<ZShaderData, ZShaderRenderer>() {

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

}

@Serializable
data class ZShaderData(
    val type: String,
    val source: String
): ZComponentData()

class ZShaderRenderer: ZComponentRender<ZShaderData> {

    var shader: GLWrap = GLWrap()

    override fun initialize(ctx: ZRenderingContext, data: ZShaderData) {
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

class ZShaderSerializer: ZComponentSerializer<ZShader, ZShaderData, ZShaderRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZShaderData>
        get() = ZShaderData.serializer()

    override fun createRendererComponent(): ZShaderRenderer {
        return ZShaderRenderer()
    }

    override fun createComponentInstance(data: ZShaderData, renderer: ZShaderRenderer): ZShader {
        return ZShader()
    }

}
