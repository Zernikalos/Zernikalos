package zernikalos.components.shader

import zernikalos.GLWrap
import zernikalos.ZDataType
import zernikalos.ZGLRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZUniformRenderer: ZComponentRender<ZUniformData> {

    lateinit var uniformId: GLWrap

    actual override fun initialize(ctx: ZRenderingContext, data: ZUniformData) {
    }

    actual fun bindLocation(ctx: ZRenderingContext, data: ZUniformData, program: ZProgram) {
        ctx as ZGLRenderingContext
        uniformId = ctx.getUniformLocation(program.renderer.programId, data.uniformName)
    }

    actual fun bindValue(ctx: ZRenderingContext, data: ZUniformData, values: FloatArray) {
        ctx as ZGLRenderingContext
        when (data.type) {
            ZDataType.MAT4F -> ctx.uniformMatrix4fv(uniformId, data.count, false, values)
            else -> return
        }
    }
}
