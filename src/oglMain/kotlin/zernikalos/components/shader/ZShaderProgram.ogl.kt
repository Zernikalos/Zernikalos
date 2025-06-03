package zernikalos.components.shader

import zernikalos.context.ExpectShaderType
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

enum class ZGlShaderType(val value: Int) {
    VERTEX_SHADER(ExpectShaderType.VERTEX_SHADER),
    FRAGMENT_SHADER(ExpectShaderType.FRAGMENT_SHADER)
}

@JsExport
actual open class ZShaderProgramRender : ZBaseShaderProgram {

    @JsExport.Ignore
    val program: ZProgram = ZProgram()

    @JsName("init")
    actual constructor(): super() {
    }

    override fun internalRenderInitialize(ctx: ZRenderingContext) {
        ctx as ZGLRenderingContext

        program.initialize(ctx)

        vertexShader.initialize(ctx)
        vertexShader.initialize(shaderSource)
        ctx.attachShader(program.renderer.programId, vertexShader.renderer.shader)

        fragmentShader.initialize(ctx)
        fragmentShader.initialize(shaderSource)
        ctx.attachShader(program.renderer.programId, fragmentShader.renderer.shader)

        attributes.values.forEach { attr ->
            attr.initialize(ctx)
            attr.bindLocation(ctx, program.renderer.programId)
        }

        program.link()
        uniforms.singles.forEach { uniform ->
            uniform.initialize(ctx)
            uniform.renderer.uniformId = ctx.getUniformLocation(
                program.renderer.programId,
                uniform.uniformName
            )
        }
    }

    actual override fun bind(ctx: ZRenderingContext) {
        program.bind()
        uniforms.singles.forEach { uniform ->
            uniform.renderer.bind()
        }
    }

    actual override fun unbind(ctx: ZRenderingContext) {
        program.unbind()
    }

}