package zernikalos.components.shader

import zernikalos.GLWrap
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZUniformRenderer: ZComponentRender<ZUniformData> {

    lateinit var uniformId: GLWrap

    actual override fun initialize(ctx: ZRenderingContext, data: ZUniformData) {
    }

    actual fun bindLocation(ctx: ZRenderingContext, data: ZUniformData, program: ZProgram) {
        uniformId = ctx.getUniformLocation(program.renderer.programId, data.uniformName)
    }

    actual fun bindValue(ctx: ZRenderingContext, data: ZUniformData, values: FloatArray) {
        when (data.type) {
            ZUniformType.MAT4 -> ctx.uniformMatrix4fv(uniformId, data.count, false, values)
            else -> return
        }
    }
}
