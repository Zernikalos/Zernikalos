package zernikalos.components.shader

import zernikalos.GLWrap
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZProgramRenderer: ZComponentRender<ZProgramData> {

    lateinit var programId: GLWrap

    actual override fun initialize(ctx: ZRenderingContext, data: ZProgramData) {
        val p = ctx.createProgram()
        // TODO
        /* if (program <= 0) {
            val err = context.getError()
            throw Error("Unable to create program ${err}")
        } */
        programId = p
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZProgramData) {
        ctx.useProgram(programId)
    }

    actual fun link(ctx: ZRenderingContext) {
        ctx.linkProgram(programId)
    }

}