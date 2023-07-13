package zernikalos.components.mesh

import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZMeshRenderer actual constructor() : ZComponentRender<ZMeshData> {
    actual fun useIndexBuffer(data: ZMeshData): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun initialize(ctx: ZRenderingContext, data: ZMeshData) {
    }

    actual override fun render(ctx: ZRenderingContext, data: ZMeshData) {
    }

}