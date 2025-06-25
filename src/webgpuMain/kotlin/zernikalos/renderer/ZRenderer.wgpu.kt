package zernikalos.renderer

import zernikalos.context.ZContext
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.*
import zernikalos.math.ZColor
import zernikalos.objects.ZModel
import zernikalos.search.findFirstModel
import zernikalos.utils.toByteArray

actual class ZRenderer actual constructor(ctx: ZContext): ZRendererBase(ctx) {

    override fun initialize() {
        ctx.scene!!.initialize(ctx)
    }

    actual fun bind() {
    }

    actual fun unbind() {
    }

    actual fun render() {
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext

        gpuCtx.createCommandEncoder()
        // TODO: Remove this thing from here
        ctx.scene!!.viewport.render()

        gpuCtx.createRenderPass(ctx.scene!!.viewport.renderer.renderPassDescriptor!!.toGpu())
        ctx.scene!!.render(ctx)
        gpuCtx.queue.submit(arrayOf(gpuCtx.commandEncoder!!.finish()))
    }

    actual override fun onViewportResize(width: Int, height: Int) {
        ctx.scene?.viewport?.onViewportResize(width, height)
    }
}
