package zernikalos.components.mesh

import zernikalos.components.ZComponentRender
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPUDevice
import zernikalos.context.webgpu.GPUBuffer
import zernikalos.context.webgpu.GPUBufferDescriptor
import zernikalos.context.webgpu.GPUBufferUsage
import zernikalos.logger.logger
import kotlinx.serialization.Transient
import zernikalos.context.ZWebGPURenderingContext

actual class ZBufferRenderer actual constructor(ctx: ZRenderingContext, data: ZBufferData) : ZComponentRender<ZBufferData>(ctx, data) {
    @Transient
    lateinit var buffer: GPUBuffer

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        // if (!data.hasData) {
        //     return
        // }

        // val usage = if (data.isIndexBuffer)
        //     GPUBufferUsage.INDEX or GPUBufferUsage.COPY_DST
        // else
        //     GPUBufferUsage.VERTEX or GPUBufferUsage.COPY_DST

        // buffer = ctx.device.createBuffer(
        //     data.dataArray.size.toLong(),
        //      usage,
        //     false,
        //     data.name
        // )

        // ctx.queue.writeBuffer(
        //     buffer,
        //     0,
        //     data.dataArray
        // )

        // logger.debug("Initializing WebGPU Buffer ${data.name}=[${data.id}]")
    }

    actual override fun bind() {
        // In WebGPU, binding is typically done during render pass setup
        // This method might be a no-op or used for specific binding requirements
    }

    actual override fun unbind() {
        // Typically not needed in WebGPU
    }
}