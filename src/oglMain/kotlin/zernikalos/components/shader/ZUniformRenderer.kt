package zernikalos.components.shader

import zernikalos.ZTypes
import zernikalos.components.ZComponentRender
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext

actual class ZUniformRenderer: ZComponentRender<ZUniformData> {

    lateinit var uniformId: GLWrap

    actual override fun initialize(ctx: ZRenderingContext, data: ZUniformData) {
    }

    actual fun bindLocation(ctx: ZRenderingContext, data: ZUniformData, program: ZProgram) {
        ctx as ZGLRenderingContext
        uniformId = ctx.getUniformLocation(program.renderer.programId, data.uniformName)
    }

    actual fun bindValue(ctx: ZRenderingContext, shaderProgram: ZShaderProgram, data: ZUniformData, values: FloatArray) {
        ctx as ZGLRenderingContext
        when (data.dataType) {
            ZTypes.MAT4F -> ctx.uniformMatrix4fv(uniformId, data.count, false, values)
            else -> return
        }
    }
}
