package zernikalos.components.shader

import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZProgramRenderer actual constructor(ctx: ZRenderingContext, data: ZProgramData): ZComponentRender<ZProgramData>(ctx, data) {

    lateinit var programId: GLWrap

    actual override fun initialize() {
        ctx as ZGLRenderingContext

        val p = ctx.createProgram()
        // TODO
        /* if (program <= 0) {
            val err = context.getError()
            throw Error("Unable to create program ${err}")
        } */
        programId = p
    }

    actual override fun bind() {
        ctx as ZGLRenderingContext

        ctx.useProgram(programId)
    }

    actual fun link() {
        ctx as ZGLRenderingContext

        ctx.linkProgram(programId)
    }

}