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
    private var currentWidth: Int = 0
    private var currentHeight: Int = 0
//    private val renderxPassDescriptor: GPURenderPassDescriptor

    // TODO: Delete
    var uniformBuffer: GPUBuffer? = null
    var pipeline: GPURenderPipeline? = null
    var bindGroup: GPUBindGroup? = null
    var depthTexture: GPUTexture? = null


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

    val model: ZModel?
        get() {
            val scene = ctx.scene ?: return null
            return findFirstModel(scene)
        }


    override fun initialize() {
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext
        super.initialize()

        ctx.scene?.initialize(ctx)

        initializeCube(gpuCtx)
        initialized = true
    }

    fun initializeCube(ctx: ZRenderingContext) {
        ctx as ZWebGPURenderingContext

        // Uniform buffer para la matriz MVP
        val uniformBufferSize = 64 // 4x4 matriz de 4 bytes cada uno
        uniformBuffer = ctx.device.createBuffer(
            uniformBufferSize,
            GPUBufferUsage.UNIFORM or GPUBufferUsage.COPY_DST,
            false,
            "uniformBuffer"
        )

        val bindGroupLayout = ctx.device.createBindGroupLayout(
            GPUBindGroupLayoutDescriptor(
                entries = arrayOf(GPUBindGroupLayoutEntry(
                    binding = 0,
                    visibility = GPUShaderStage.VERTEX,
                    buffer = GPUBufferBindingLayout(
                        type = GPUBufferBindingType.UNIFORM
                    ),
                )),
                label = "bindGroupLayout"
            ).toGpu()
        )

        bindGroup = ctx.device.createBindGroup(
            GPUBindGroupDescriptor(
                layout = bindGroupLayout,
                entries = arrayOf(
                    GPUBindGroupEntry(
                        binding = 0,
                        resource = GPUBindGroupResource(
                            buffer = uniformBuffer!!
                        )
                    )
                )
            ).toGpu()
        )

        val renderPipelineDescriptor = GPURenderPipelineDescriptor(
            layout = ctx.device.createPipelineLayout(
                GPUPipelineLayoutDescriptor(
                    bindGroupLayouts = arrayOf(bindGroupLayout)
                )
            ),
            vertex = GPUVertexState(
                module = model?.shaderProgram?.renderer?.shaderModule!!,
                entryPoint = "vs_main",
                buffers = model?.mesh?.renderer?.vertexBuffersLayout
            ),
            fragment = GPUFragmentState(
                module = model?.shaderProgram?.renderer?.shaderModule!!,
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

        pipeline = ctx.device.createRenderPipeline(renderPipelineDescriptor.toGpu())

        // Buffer para profundidad
        depthTexture = ctx.device.createTexture(
            GPUTextureDescriptor(
                size = GPUExtent3D(
                    width = ctx.surfaceView.surfaceWidth,
                    height = ctx.surfaceView.surfaceHeight
                ),
                format = GPUTextureFormat.Depth24Plus,
                usage = GPUTextureUsage.RENDER_ATTACHMENT
            ).toGpu()
        )
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
        if (uniformBuffer == null || pipeline == null) {
            return
        }
        val model = findFirstModel(ctx.scene!!) ?: return
        // ctx.scene?.render(ctx)

        //val mvp = ZMatrix4(arrayOf<Float>(1.0867713689804077f, -0.7829893231391907f, -0.6631385087966919f, -0.6624753475189209f, 0f, 2.1683130264282227f, -0.44014039635658264f, -0.43970024585723877f, -1.1871562004089355f, -0.7167804837226868f, -0.6070641279220581f, -0.6064570546150208f, -1.1871559619903564f, -0.7167804837226868f, 2.7076656818389893f, 2.8049581050872803f))

        val mvp = ctx.activeCamera!!.viewProjectionMatrix * model.transform.matrix

        gpuCtx.queue.writeBuffer(uniformBuffer!!, 0, mvp.floatArray.toByteArray(true));


        //val commandEncoder = gpuCtx.device.createCommandEncoder();
        gpuCtx.createCommandEncoder()
        val textureView = gpuCtx.webGPUContext?.getCurrentTexture()?.createView();
        val depthView = depthTexture?.createView();

        val clearColor = ZColor(0.5f, 0.5f, 0.5f, 1.0f)
        val colorAttachment = GPURenderPassColorAttachment(
            view = textureView!!,
            loadOp = "clear",
            storeOp = "store",
            clearValue = GPUColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
        )

        val depthAttachment = GPURenderPassDepthStencilAttachment(
            view = depthView!!,
            depthLoadOp = "clear",
            depthStoreOp = "store",
            depthClearValue = 1.0f
        )

        val renderPassDescriptor = GPURenderPassDescriptor(
            colorAttachments = arrayOf(colorAttachment),
            depthStencilAttachment = depthAttachment
        )

        //val renderPass = commandEncoder.beginRenderPass(renderPassDescriptor)
        gpuCtx.createRenderPass(renderPassDescriptor)
        gpuCtx.renderPass?.setPipeline(pipeline!!)
        model.mesh.bind()
        gpuCtx.renderPass?.setBindGroup(0, bindGroup!!)
        model.mesh.render()
        gpuCtx.renderPass?.end()

        gpuCtx.queue.submit(arrayOf(gpuCtx.commandEncoder!!.finish()))
    }

    actual override fun onViewportResize(width: Int, height: Int) {
        currentWidth = width
        currentHeight = height
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext
//        gpuCtx.resizeCanvas(width, height)
    }
}
