package zernikalos.components.buffer

import zernikalos.GLWrap
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

class ZVertexArrayRenderer: ZComponentRender<ZVertexArrayData> {

    private lateinit var vao: GLWrap

    override fun initialize(ctx: ZRenderingContext, data: ZVertexArrayData) {
        val auxVao = ctx.createVertexArray()
        // TODO Check existence
        vao = auxVao
        ctx.bindVertexArray(vao)
    }

    override fun bind(ctx: ZRenderingContext, data: ZVertexArrayData) {
        ctx.bindVertexArray(vao)
    }

    override fun unbind(ctx: ZRenderingContext, data: ZVertexArrayData) {
    }

}
