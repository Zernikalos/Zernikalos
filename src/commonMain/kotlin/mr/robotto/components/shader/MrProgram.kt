package mr.robotto.components.shader

import mr.robotto.GLWrap
import mr.robotto.components.MrComponent

class MrProgram: MrComponent() {

    lateinit var program: GLWrap

    override fun renderInitialize() {
        val p = createProgram()
        // TODO
        /* if (program <= 0) {
            val err = context.getError()
            throw Error("Unable to create program ${err}")
        } */
        program = p
    }

    override fun render() {
        context.useProgram(program)
    }

    fun link() {
        context.linkProgram(program)
    }

    private fun createProgram(): GLWrap {
        return context.createProgram()
    }

}