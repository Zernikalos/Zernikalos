package zernikalos.components.shader

import zernikalos.components.ZBindeable
import zernikalos.components.ZComponent
import zernikalos.components.ZComponentData
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender
import kotlin.js.JsName

class ZProgram internal constructor(data: ZProgramData): ZComponent<ZProgramData, ZProgramRenderer>(data), ZBindeable {

    @JsName("init")
    constructor(): this(ZProgramData())

    override fun createRenderer(ctx: ZRenderingContext): ZProgramRenderer {
        return ZProgramRenderer(ctx, data)
    }

    override fun bind() {
        renderer.bind()
    }

    override fun unbind() {
    }

    fun link() {
        renderer.link()
    }

}

class ZProgramData(): ZComponentData() {

    override fun toString(): String {
        return ""
    }
}

class ZProgramRenderer(ctx: ZRenderingContext, data: ZProgramData): ZComponentRender<ZProgramData>(ctx, data) {

    lateinit var programId: GLWrap

    override fun initialize() {
        ctx as ZGLRenderingContext

        val p = ctx.createProgram()
        // TODO
        /* if (program <= 0) {
            val err = context.getError()
            throw Error("Unable to create program ${err}")
        } */
        programId = p
    }

    override fun bind() {
        ctx as ZGLRenderingContext

        ctx.useProgram(programId)
    }

    fun link() {
        ctx as ZGLRenderingContext

        ctx.linkProgram(programId)
    }

}