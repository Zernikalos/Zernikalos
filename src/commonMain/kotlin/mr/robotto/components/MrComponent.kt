package mr.robotto.components

import mr.robotto.MrRenderingContext

abstract class MrComponent {

    abstract fun initialize(ctx: MrRenderingContext)

    abstract fun render(ctx: MrRenderingContext)

}
