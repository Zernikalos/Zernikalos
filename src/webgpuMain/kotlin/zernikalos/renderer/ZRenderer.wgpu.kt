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
        super.initialize()

        ctx.scene?.initialize(ctx)

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
        if (!initialized) {
            throw Error("TU TAS LOCO")
        }
        renderCube()
    }

    fun renderCube() {
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext

        val model = findFirstModel(ctx.scene!!) ?: return
        // ctx.scene?.render(ctx)

        //val mvp = ZMatrix4(arrayOf<Float>(1.0867713689804077f, -0.7829893231391907f, -0.6631385087966919f, -0.6624753475189209f, 0f, 2.1683130264282227f, -0.44014039635658264f, -0.43970024585723877f, -1.1871562004089355f, -0.7167804837226868f, -0.6070641279220581f, -0.6064570546150208f, -1.1871559619903564f, -0.7167804837226868f, 2.7076656818389893f, 2.8049581050872803f))

        val mvp = ctx.activeCamera!!.viewProjectionMatrix * model.transform.matrix

        gpuCtx.queue.writeBuffer(model.renderer.uniformBuffer!!, 0, mvp.floatArray.toByteArray(true));

        //val commandEncoder = gpuCtx.device.createCommandEncoder();
        gpuCtx.createCommandEncoder()

        ctx.scene!!.viewport.render()

        gpuCtx.createRenderPass(ctx.scene!!.viewport.renderer.renderPassDescriptor!!.toGpu())
        model.render(ctx)
        gpuCtx.queue.submit(arrayOf(gpuCtx.commandEncoder!!.finish()))
    }

    actual override fun onViewportResize(width: Int, height: Int) {
        ctx.scene?.viewport?.onViewportResize(width, height)
    }
}
