package zernikalos.components.mesh

import zernikalos.context.ZRenderingContext
import zernikalos.context.webgpu.GPUBuffer
import zernikalos.context.webgpu.GPUBufferUsage
import kotlinx.serialization.Transient
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZWebGPURenderingContext

actual class ZBufferRenderer actual constructor(ctx: ZRenderingContext, private val data: ZBufferData) : ZComponentRenderer(ctx) {
    @Transient
    lateinit var wgpuBuffer: GPUBuffer

    val usage: Int
    get() {
        return if (data.isIndexBuffer)
            GPUBufferUsage.INDEX or GPUBufferUsage.COPY_DST
        else
            GPUBufferUsage.VERTEX or GPUBufferUsage.COPY_DST
    }

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        // Each buffer will create a different GPUBuffer instance
        wgpuBuffer = ctx.device.createBuffer(
            data.dataArray.size * data.dataType.byteSize,
            usage,
            false,
            "${data.name}Buffer"
        )
        ctx.queue.writeBuffer(wgpuBuffer, 0, data.dataArray)
    }

    actual override fun bind() {
        // In WebGPU, binding is typically done during render pass setup
        // This method might be a no-op or used for specific binding requirements
    }

    actual override fun unbind() {
        // Typically not needed in WebGPU
    }
}
