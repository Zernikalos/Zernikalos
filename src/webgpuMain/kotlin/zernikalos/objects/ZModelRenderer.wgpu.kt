package zernikalos.objects

import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.GPUBindGroup
import zernikalos.context.webgpu.GPUBindGroupDescriptor
import zernikalos.context.webgpu.GPUBindGroupLayoutDescriptor
import zernikalos.context.webgpu.GPUColorTargetState
import zernikalos.context.webgpu.GPUCompareFunction
import zernikalos.context.webgpu.GPUCullMode
import zernikalos.context.webgpu.GPUDepthStencilState
import zernikalos.context.webgpu.GPUFragmentState
import zernikalos.context.webgpu.GPUPipelineLayoutDescriptor
import zernikalos.context.webgpu.GPUPrimitiveState
import zernikalos.context.webgpu.GPUPrimitiveTopology
import zernikalos.context.webgpu.GPURenderPipeline
import zernikalos.context.webgpu.GPURenderPipelineDescriptor
import zernikalos.context.webgpu.GPUTextureFormat
import zernikalos.context.webgpu.GPUVertexState

actual class ZModelRenderer actual constructor(private val ctx: ZRenderingContext, private val model: ZModel) {

    var pipeline: GPURenderPipeline? = null
    var bindGroup: GPUBindGroup? = null

    actual fun initialize() {
        ctx as ZWebGPURenderingContext

        val bindGroupLayoutEntries = model.shaderProgram.uniforms.blocks.asSequence().map { block ->
            block.renderer.bindGroupLayoutEntry!!
        }.toList()

        val bindGroupLayout = ctx.device.createBindGroupLayout(
            GPUBindGroupLayoutDescriptor(
                entries = bindGroupLayoutEntries.toTypedArray(),
                label = "bindGroupLayout"
            ).toGpu()
        )

        val bindGroupEntries = model.shaderProgram.uniforms.blocks.asSequence().map { block ->
            block.renderer.bindGroupEntry!!
        }.toList()

        bindGroup = ctx.device.createBindGroup(
            GPUBindGroupDescriptor(
                layout = bindGroupLayout,
                entries = bindGroupEntries.toTypedArray()
            ).toGpu()
        )

        val renderPipelineDescriptor = GPURenderPipelineDescriptor(
            layout = ctx.device.createPipelineLayout(
                GPUPipelineLayoutDescriptor(
                    bindGroupLayouts = arrayOf(bindGroupLayout)
                )
            ),
            vertex = GPUVertexState(
                module = model.shaderProgram.renderer.shaderModule!!,
                entryPoint = "vs_main",
                buffers = model.mesh.renderer.vertexBuffersLayout
            ),
            fragment = GPUFragmentState(
                module = model.shaderProgram.renderer.shaderModule!!,
                entryPoint = "fs_main",
                targets = arrayOf(GPUColorTargetState(
                    format = ctx.getPreferredCanvasFormat().toString()
                ))
            ),
            depthStencil = GPUDepthStencilState(
                format = GPUTextureFormat.Depth24Plus,
                depthWriteEnabled = true,
                depthCompare = GPUCompareFunction.LESS
            ),
            primitive = GPUPrimitiveState(
                cullMode = GPUCullMode.NONE,
                topology = GPUPrimitiveTopology.TRIANGLE_LIST
            )
        )

        println("Render pipeline descriptor: ${JSON.stringify(renderPipelineDescriptor.toGpu())}")
        pipeline = ctx.device.createRenderPipeline(renderPipelineDescriptor.toGpu())

    }

    actual fun render() {
        ctx as ZWebGPURenderingContext

        if (pipeline == null) {
            return
        }
        ctx.renderPass?.setPipeline(pipeline!!)
        ctx.renderPass?.setBindGroup(0, bindGroup!!)

        model.shaderProgram.bind()
        model.mesh.bind()
        model.mesh.render()
        ctx.renderPass?.end()
    }
}
