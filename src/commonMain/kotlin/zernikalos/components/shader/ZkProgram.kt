package zernikalos.components.shader

import zernikalos.GLWrap
import zernikalos.ZkRenderingContext
import zernikalos.components.ZkComponent

class ZkProgram: ZkComponent() {

    lateinit var programId: GLWrap

    override fun initialize(ctx: ZkRenderingContext) {
        val p = ctx.createProgram()
        // TODO
        /* if (program <= 0) {
            val err = context.getError()
            throw Error("Unable to create program ${err}")
        } */
        programId = p
    }

    override fun render(ctx: ZkRenderingContext) {
        ctx.useProgram(programId)
    }

    fun link(ctx: ZkRenderingContext) {
        ctx.linkProgram(programId)
    }

}