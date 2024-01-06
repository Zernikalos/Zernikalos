package zernikalos.components.shader

import zernikalos.context.ZRenderingContext
import zernikalos.components.*
import kotlin.js.JsName
import kotlin.random.Random

class ZProgram internal constructor(data: ZProgramData): ZComponent<ZProgramData, ZProgramRenderer>(data), ZBindeable {

    @JsName("init")
    constructor(): this(ZProgramData())

    override fun internalInitialize(ctx: ZRenderingContext) {
        renderer.initialize()
    }

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

    override fun hashCode(): Int {
        return Random.nextInt()
    }

    override fun toString(): String {
        return ""
    }
}

expect class ZProgramRenderer(ctx: ZRenderingContext, data: ZProgramData): ZComponentRender<ZProgramData> {

    override fun initialize()

    override fun bind()

    fun link()

}