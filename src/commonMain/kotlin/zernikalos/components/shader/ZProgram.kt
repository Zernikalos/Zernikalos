package zernikalos.components.shader

import zernikalos.GLWrap
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponent

class ZProgram: ZComponent() {

    lateinit var programId: GLWrap

    override fun initialize(ctx: ZRenderingContext) {
        val p = ctx.createProgram()
        // TODO
        /* if (program <= 0) {
            val err = context.getError()
            throw Error("Unable to create program ${err}")
        } */
        programId = p
    }

    override fun render(ctx: ZRenderingContext) {
        ctx.useProgram(programId)
    }

    fun link(ctx: ZRenderingContext) {
        ctx.linkProgram(programId)
    }

}