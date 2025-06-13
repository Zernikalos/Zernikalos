package zernikalos.components.mesh

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext

actual class ZMeshRenderer actual constructor(
    ctx: ZRenderingContext,
    data: ZMeshData
) : ZComponentRenderer(ctx) {
    actual override fun initialize() {
    }

    actual override fun render() {
    }
}
