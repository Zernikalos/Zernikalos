package zernikalos.components.mesh

import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponent
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentRender
import zernikalos.logger.logger

class ZVertexArray internal constructor(data: ZVertexArrayData): ZComponent<ZVertexArrayData, ZVertexArrayRenderer>(data), ZBindeable {

    constructor(): this(ZVertexArrayData())

    override fun internalInitialize(ctx: ZRenderingContext) {
        renderer.initialize()
    }

    override fun createRenderer(ctx: ZRenderingContext): ZVertexArrayRenderer {
        return ZVertexArrayRenderer(ctx, data)
    }

    override fun bind() {
        renderer.bind()
    }

    override fun unbind() {
        renderer.unbind()
    }

}

class ZVertexArrayData: ZComponentData() {

    override fun toString(): String {
        return ""
    }

}
class ZVertexArrayRenderer(ctx: ZRenderingContext, data: ZVertexArrayData): ZComponentRender<ZVertexArrayData>(ctx, data) {

    private lateinit var vao: GLWrap

    override fun initialize() {
        ctx as ZGLRenderingContext

        val auxVao = ctx.createVertexArray()
        // TODO Check existence
        vao = auxVao
        ctx.bindVertexArray(vao)
        logger.debug("Creating and binding VAO with id ${vao.id}")
    }

    override fun bind() {
        ctx as ZGLRenderingContext

        logger.debugOnce("Binding VAO with id ${vao.id}")
        ctx.bindVertexArray(vao)
    }

    override fun unbind() {
    }

}