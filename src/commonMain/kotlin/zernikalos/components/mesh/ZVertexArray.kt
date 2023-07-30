package zernikalos.components.mesh

import zernikalos.ZRenderingContext
import zernikalos.components.*

class ZVertexArray: ZComponent<ZVertexArrayData, ZVertexArrayRenderer>(), ZBindeable {

    init {
        data = ZVertexArrayData()
        renderer = ZVertexArrayRenderer()
    }

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

    override fun bind(ctx: ZRenderingContext) {
        renderer.bind(ctx, data)
    }

    override fun unbind(ctx: ZRenderingContext) {
        renderer.unbind(ctx, data)
    }

}

class ZVertexArrayData: ZComponentData()

expect class ZVertexArrayRenderer(): ZComponentRender<ZVertexArrayData> {

    override fun initialize(ctx: ZRenderingContext, data: ZVertexArrayData)

    override fun bind(ctx: ZRenderingContext, data: ZVertexArrayData)

    override fun unbind(ctx: ZRenderingContext, data: ZVertexArrayData)

}
