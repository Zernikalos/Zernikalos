package zernikalos.components.shader

import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZShaderProgramRenderer: ZComponentRender<ZShaderProgramData> {

    actual val program: ZProgram = ZProgram()

    actual override fun initialize(ctx: ZRenderingContext, data: ZShaderProgramData) {
        program.initialize(ctx)

        data.vertexShader.initialize(ctx)
        attachShader(ctx, program, data.vertexShader)

        data.fragmentShader.initialize(ctx)
        attachShader(ctx, program, data.fragmentShader)

        data.attributes.values.forEach { attr ->
            attr.initialize(ctx)
            attr.bindLocation(ctx, program)
        }

        program.link(ctx)
        data.uniforms.values.forEach { uniform ->
            uniform.initialize(ctx)
            uniform.bindLocation(ctx, program)
        }
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZShaderProgramData) {
        program.bind(ctx)
    }

    actual override fun unbind(ctx: ZRenderingContext, data: ZShaderProgramData) {
        program.unbind(ctx)
    }

    private fun attachShader(ctx: ZRenderingContext, program: ZProgram, shader: ZShader) {
        ctx as ZGLRenderingContext

        ctx.attachShader(program.renderer.programId, shader.renderer.shader)
    }

}