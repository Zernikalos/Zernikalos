package zernikalos.components.shader

import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender
import zernikalos.context.GLWrap

actual class ZShaderProgramRenderer actual constructor(ctx: ZRenderingContext, data: ZShaderProgramData): ZComponentRender<ZShaderProgramData>(ctx, data) {

    val program: ZProgram = ZProgram()

    val programId: GLWrap
        get() = program.renderer.programId

    actual override fun initialize() {
        program.initialize(ctx)

        data.vertexShader.initialize(ctx)
        attachShader(data.vertexShader)

        data.fragmentShader.initialize(ctx)
        attachShader(data.fragmentShader)

        data.attributes.values.forEach { attr ->
            attr.initialize(ctx)
            attr.renderer.bindLocation(programId)
        }

        program.link()
        data.uniforms.values.forEach { uniform ->
            uniform.initialize(ctx)
            uniform.renderer.bindLocation(programId)
        }
    }

    actual override fun bind() {
        program.bind()
    }

    actual override fun unbind() {
        program.unbind()
    }

    private fun attachShader(shader: ZShader) {
        ctx as ZGLRenderingContext

        ctx.attachShader(program.renderer.programId, shader.renderer.shader)
    }

}