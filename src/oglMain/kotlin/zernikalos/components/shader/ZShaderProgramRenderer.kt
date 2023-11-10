package zernikalos.components.shader

import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZShaderProgramRenderer actual constructor(ctx: ZRenderingContext, data: ZShaderProgramData): ZComponentRender<ZShaderProgramData>(ctx, data) {

    actual val program: ZProgram = ZProgram()

    actual override fun initialize() {
        program.initialize(ctx)

        data.vertexShader.initialize(ctx)
        attachShader(data.vertexShader)

        data.fragmentShader.initialize(ctx)
        attachShader(data.fragmentShader)

        data.attributes.values.forEach { attr ->
            attr.initialize(ctx)
            attr.bindLocation(program)
        }

        program.link()
        data.uniforms.values.forEach { uniform ->
            uniform.initialize(ctx)
            uniform.bindLocation(program)
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