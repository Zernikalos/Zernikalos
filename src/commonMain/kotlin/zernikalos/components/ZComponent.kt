package zernikalos.components

import zernikalos.ZRenderingContext

abstract class ZComponent {

    abstract fun initialize(ctx: ZRenderingContext)

    abstract fun render(ctx: ZRenderingContext)

}
