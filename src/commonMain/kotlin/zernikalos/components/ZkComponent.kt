package zernikalos.components

import zernikalos.ZkRenderingContext

abstract class ZkComponent {

    abstract fun initialize(ctx: ZkRenderingContext)

    abstract fun render(ctx: ZkRenderingContext)

}
