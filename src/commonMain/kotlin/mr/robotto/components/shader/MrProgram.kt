package mr.robotto.components.shader

import mr.robotto.GLWrap
import mr.robotto.components.MrComponent

class MrProgram: MrComponent() {

    lateinit var programId: GLWrap

    override fun renderInitialize() {
        val p = createProgram()
        // TODO
        /* if (program <= 0) {
            val err = context.getError()
            throw Error("Unable to create program ${err}")
        } */
        programId = p
    }

    override fun render() {
        context.useProgram(programId)
    }

    fun link() {
        context.linkProgram(programId)
    }

    private fun createProgram(): GLWrap {
        return context.createProgram()
    }

}