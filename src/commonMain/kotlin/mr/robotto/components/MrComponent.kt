package mr.robotto.components

import mr.robotto.MrRenderingContext

abstract class MrComponent {

    protected lateinit var context: MrRenderingContext

    fun initialize(ctx: MrRenderingContext) {
        context = ctx
        renderInitialize()
    }

    protected abstract fun renderInitialize()

    abstract fun render()
}
