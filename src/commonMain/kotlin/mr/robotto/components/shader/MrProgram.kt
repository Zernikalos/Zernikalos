package mr.robotto.components.shader

import mr.robotto.GLWrap
import mr.robotto.MrRenderingContext
import mr.robotto.components.MrComponent

class MrProgram: MrComponent() {

    lateinit var programId: GLWrap

    override fun initialize(ctx: MrRenderingContext) {
        val p = ctx.createProgram()
        // TODO
        /* if (program <= 0) {
            val err = context.getError()
            throw Error("Unable to create program ${err}")
        } */
        programId = p
    }

    override fun render(ctx: MrRenderingContext) {
        ctx.useProgram(programId)
    }

    fun link(ctx: MrRenderingContext) {
        ctx.linkProgram(programId)
    }

}