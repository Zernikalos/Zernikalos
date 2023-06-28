package zernikalos.components.shader

import zernikalos.GLWrap
import zernikalos.ZRenderingContext
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponent

class ZProgram: ZBindeable() {

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

    override fun bind(ctx: ZRenderingContext) {
        ctx.useProgram(programId)
    }

    override fun unbind(ctx: ZRenderingContext) {
    }

    fun link(ctx: ZRenderingContext) {
        ctx.linkProgram(programId)
    }

}