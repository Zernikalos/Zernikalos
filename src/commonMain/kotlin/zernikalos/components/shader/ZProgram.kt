package zernikalos.components.shader

import zernikalos.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsName

class ZProgram internal constructor(data: ZProgramData, renderer: ZProgramRenderer): ZComponent<ZProgramData, ZProgramRenderer>(data, renderer), ZBindeable {

    @JsName("init")
    constructor(): this(ZProgramData(), ZProgramRenderer())

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

expect class ZProgramRenderer(): ZComponentRender<ZProgramData> {

    override fun initialize(ctx: ZRenderingContext, data: ZProgramData)

    override fun bind(ctx: ZRenderingContext, data: ZProgramData)

    fun link(ctx: ZRenderingContext)

}