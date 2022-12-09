package mr.robotto.components.shader

import mr.robotto.GLWrap
import mr.robotto.components.MrComponent
import mr.robotto.components.MrComponentData
import mr.robotto.components.MrComponentRender

class MrProgram: MrComponent<MrProgramData, MrProgramRender>() {

    override var data: MrProgramData = MrProgramData()
    override var renderer: MrProgramRender = MrProgramRender()

    val program: GLWrap
        get() = data.program

    fun link() {
        renderer.link()
    }
}

class MrProgramData: MrComponentData() {
    lateinit var program: GLWrap
}

class MrProgramRender: MrComponentRender<MrProgramData>() {

    override fun internalInitialize() {
        val program = createProgram()
        /* if (program <= 0) {
            val err = context.getError()
            throw Error("Unable to create program ${err}")
        } */
        data.program = program
    }

    override fun render() {
        context.useProgram(data.program)
    }

    fun link() {
        context.linkProgram(data.program)
    }

    private fun createProgram(): GLWrap {
        return context.createProgram()
    }

}