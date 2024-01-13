package zernikalos.components.shader

import zernikalos.ZTypes
import zernikalos.components.ZComponentRender
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext

actual class ZUniformRenderer actual constructor(ctx: ZRenderingContext, data: ZUniformData): ZComponentRender<ZUniformData>(ctx, data) {

    lateinit var uniformId: GLWrap

    actual override fun initialize() {
    }

    fun bindLocation(programId: GLWrap) {
        ctx as ZGLRenderingContext
        uniformId = ctx.getUniformLocation(programId, data.uniformName)
    }

    actual fun bindValue(shaderProgram: ZShaderProgram, values: FloatArray) {
        ctx as ZGLRenderingContext
        when (data.dataType) {
            ZTypes.MAT4F -> ctx.uniformMatrix4fv(uniformId, data.count, false, values)
            else -> return
        }
    }
}
