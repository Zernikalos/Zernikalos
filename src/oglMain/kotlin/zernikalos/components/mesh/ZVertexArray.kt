package zernikalos.components.mesh

import zernikalos.GLWrap
import zernikalos.ZGLRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponent
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentRender

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
        ctx as ZGLRenderingContext

        val auxVao = ctx.createVertexArray()
        // TODO Check existence
        vao = auxVao
        ctx.bindVertexArray(vao)
    }

    override fun bind(ctx: ZRenderingContext, data: ZVertexArrayData) {
        ctx as ZGLRenderingContext

        ctx.bindVertexArray(vao)
    }

    override fun unbind(ctx: ZRenderingContext, data: ZVertexArrayData) {
    }

}