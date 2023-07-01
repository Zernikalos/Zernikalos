package zernikalos.components.shader

import zernikalos.GLWrap
import zernikalos.ZRenderingContext
import zernikalos.components.*

class ZProgram: ZComponent<ZProgramData, ZProgramRenderer>(), ZBindeable {

    init {
        data = ZProgramData()
        renderer = ZProgramRenderer()
    }

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

    override fun bind(ctx: ZRenderingContext) {
        renderer.bind(ctx, data)
    }

    override fun unbind(ctx: ZRenderingContext) {
    }

    fun link(ctx: ZRenderingContext) {
        renderer.link(ctx)
    }

}

class ZProgramData(): ZComponentData()

class ZProgramRenderer: ZComponentRender<ZProgramData> {

    lateinit var programId: GLWrap

    override fun initialize(ctx: ZRenderingContext, data: ZProgramData) {
        val p = ctx.createProgram()
        // TODO
        /* if (program <= 0) {
            val err = context.getError()
            throw Error("Unable to create program ${err}")
        } */
        programId = p
    }

    override fun bind(ctx: ZRenderingContext, data: ZProgramData) {
        ctx.useProgram(programId)
    }

    fun link(ctx: ZRenderingContext) {
        ctx.linkProgram(programId)
    }

}