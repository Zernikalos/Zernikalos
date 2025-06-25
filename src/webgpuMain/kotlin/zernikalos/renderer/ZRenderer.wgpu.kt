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
    var initialized = false

    init {
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext
//        renderPassDescriptor = GPURenderPassDescriptor(
//            colorAttachments = arrayOf(
//                GPURenderPassColorAttachment(
//                    view = gpuCtx.getCurrentTextureView(),
//                    clearValue = GPUColor(r = 0.0f, g = 0.0f, b = 0.0f, a = 1.0f),
//                    loadOp = GPULoadOp.CLEAR,
//                    storeOp = GPUStoreOp.STORE
//                )
//            ),
//            depthStencilAttachment = GPURenderPassDepthStencilAttachment(
//                view = gpuCtx.depthTextureView,
//                depthClearValue = 1.0f,
//                depthLoadOp = GPULoadOp.CLEAR,
//                depthStoreOp = GPUStoreOp.STORE
//            )
//        )
    }

    override fun initialize() {
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext
        ctx.scene!!.initialize(ctx)
        initialized = true
    }

    actual fun bind() {
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext
//        gpuCtx.makeCommandBuffer()
//        gpuCtx.makeRenderCommandEncoder(renderPassDescriptor)
    }

    actual fun unbind() {
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext
//        gpuCtx.submitCommands()
    }

    actual fun render() {
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext

        if (!initialized) {
            throw Error("Renderer not initialized!")
        }
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
