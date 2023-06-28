package zernikalos.components

import zernikalos.ZRenderingContext

abstract class ZComponent {

    abstract fun initialize(ctx: ZRenderingContext)

}

abstract class ZBindeable: ZComponent() {

    abstract fun bind(ctx: ZRenderingContext)

    abstract fun unbind(ctx: ZRenderingContext)

}

abstract class ZRenderizable: ZComponent() {

    abstract fun render(ctx: ZRenderingContext)

}
