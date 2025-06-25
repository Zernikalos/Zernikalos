package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.GPUBindGroupEntry
import zernikalos.context.webgpu.GPUBindGroupLayoutEntry
import zernikalos.context.webgpu.GPUBindGroupResource
import zernikalos.context.webgpu.GPUBuffer
import zernikalos.context.webgpu.GPUBufferBindingLayout
import zernikalos.context.webgpu.GPUBufferBindingType
import zernikalos.context.webgpu.GPUBufferUsage
import zernikalos.context.webgpu.GPUShaderStage

actual class ZUniformBlockRenderer actual constructor(ctx: ZRenderingContext, private val data: ZUniformBlockData): ZComponentRenderer(ctx) {

    var uniformBuffer: GPUBuffer? = null

    var bindGroupLayoutEntry: GPUBindGroupLayoutEntry? = null
    var bindGroupEntry: GPUBindGroupEntry? = null

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        uniformBuffer = ctx.device.createBuffer(
            data.byteSize,
            GPUBufferUsage.UNIFORM or GPUBufferUsage.COPY_DST,
            false,
            "uniformBuffer-${data.uniformBlockName}"
        )

        // TODO: Review options
        bindGroupLayoutEntry = GPUBindGroupLayoutEntry(
            binding = data.id,
            visibility = GPUShaderStage.VERTEX,
            buffer = GPUBufferBindingLayout(
                type = GPUBufferBindingType.UNIFORM
            )
        )

        bindGroupEntry = GPUBindGroupEntry(
            binding = data.id,
            resource = GPUBindGroupResource(
                buffer = uniformBuffer!!
            )
        )
    }

    actual override fun bind() {
        ctx as ZWebGPURenderingContext
        ctx.queue.writeBuffer(uniformBuffer!!, 0, data.value.byteArray)
    }

    actual override fun unbind() {
    }
}
