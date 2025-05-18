package zernikalos.renderer

import kotlinx.browser.window
import zernikalos.context.ZContext
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.*
import zernikalos.math.ZMatrix4
import zernikalos.utils.toByteArray

actual class ZRenderer actual constructor(ctx: ZContext): ZRendererBase(ctx) {
    private var currentWidth: Int = 0
    private var currentHeight: Int = 0
//    private val renderxPassDescriptor: GPURenderPassDescriptor

    // TODO: Delete
    var uniformBuffer: GPUBuffer? = null
    var pipeline: GPURenderPipeline? = null
    var vertexBuffer: GPUBuffer? = null
    var indexBuffer: GPUBuffer? = null
    var bindGroup: GPUBindGroup? = null
    var depthTexture: GPUTexture? = null

    val cubeIndices = shortArrayOf(
        // Cara trasera (sentido horario)
        0, 3, 2, 2, 1, 0,
        // Cara delantera (sentido horario)
        4, 5, 6, 6, 7, 4,
        // Cara izquierda (sentido horario)
        0, 4, 7, 7, 3, 0,
        // Cara derecha (sentido horario)
        1, 2, 6, 6, 5, 1,
        // Cara superior (sentido horario)
        3, 7, 6, 6, 2, 3,
        // Cara inferior (sentido horario)
        0, 1, 5, 5, 4, 0
    )

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
        initializeCube(gpuCtx)
    }

    fun initializeCube(ctx: ZRenderingContext) {
        ctx as ZWebGPURenderingContext
        val cubeVertices = floatArrayOf(
            // X,    Y,    Z
            -1f, -1f, -1f, // 0
            1f, -1f, -1f, // 1
            1f,  1f, -1f, // 2
            -1f,  1f, -1f, // 3
            -1f, -1f,  1f, // 4
            1f, -1f,  1f, // 5
            1f,  1f,  1f, // 6
            -1f,  1f,  1f  // 7
        )

        // Índices para los triángulos del cubo


        // Buffer de vértices
        vertexBuffer = ctx.device.createBuffer(
            cubeVertices.size * 4,
            GPUBufferUsage.VERTEX or GPUBufferUsage.COPY_DST,
            false,
            "vertexBuffer"
        )
        ctx.queue.writeBuffer(vertexBuffer!!, 0, cubeVertices)

        // Buffer de índices
        indexBuffer = ctx.device.createBuffer(
            cubeIndices.size * 2,
            GPUBufferUsage.INDEX or GPUBufferUsage.COPY_DST,
            false,
            "indexBuffer"
        )
        ctx.queue.writeBuffer(indexBuffer!!, 0, cubeIndices)

        // Shader WGSL (vertex y fragment)
        val shaderCode = """
            struct Uniforms {
                modelViewProjectionMatrix : mat4x4<f32>
            }
            @binding(0) @group(0) var<uniform> uniforms : Uniforms;

            struct VertexOutput {
                @builtin(position) Position : vec4<f32>,
                @location(0) vColor : vec3<f32>
            }

            @vertex
            fn vs_main(@location(0) position : vec3<f32>) -> VertexOutput {
                var output : VertexOutput;
                output.Position = uniforms.modelViewProjectionMatrix * vec4<f32>(position, 1.0);
                output.vColor = position * 0.5 + vec3<f32>(0.5, 0.5, 0.5); // Color basado en posición
                return output;
            }

            @fragment
            fn fs_main(@location(0) vColor : vec3<f32>) -> @location(0) vec4<f32> {
                return vec4<f32>(vColor, 1.0);
            }"""

        // Compilamos shader
        val shaderModule = ctx.device.createShaderModule(shaderCode)

        // Esto va dentro del mesh, en la lista de atributos debes agregar cada uno de ellos
        // Podrias crear una property para exponer esos datos si fuera necesario creo desde el bufferobj
        // Layout de los buffers
        val vertexBuffers = arrayOf<GPUVertexBufferLayout>(
            GPUVertexBufferLayout(
                attributes = arrayOf<GPUVertexAttribute>(GPUVertexAttribute(
                    format =GPUVertexFormat.FLOAT32X3,
                    offset =0,
                    shaderLocation = 0
                )),
                arrayStride = 12,
                stepMode = GPUVertexStepMode.VERTEX
            )
        )

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
                module = shaderModule,
                entryPoint = "vs_main",
                buffers = vertexBuffers
            ),
            fragment = GPUFragmentState(
                module = shaderModule,
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


    fun initializeTriangle() {
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext



       // Paso 8: Definir los shaders (programas que se ejecutan en la GPU)
       // Shader de vértices: procesa cada vértice y determina su posición final
       // Shader de fragmentos: determina el color de cada píxel
       val shaderCode = """
           @vertex
           fn vertexMain(@location(0) position: vec2f) -> @builtin(position) vec4f {
               return vec4f(position, 0.0, 1.0);
           }

           @fragment
           fn fragmentMain() -> @location(0) vec4f {
               return vec4f(1.0, 0.0, 0.0, 1.0);
           }
       """

       // Paso 9: Crear el módulo de shader
       val shaderModule = gpuCtx.device.createShaderModule(
            shaderCode
       )

        val vertices = floatArrayOf(
            0.0f,  0.5f,  // Vértice superior
            -0.5f, -0.5f,  // Vértice inferior izquierdo
            0.5f, -0.5f   // Vértice inferior derecho
        )

        // Paso 6: Crear un buffer para almacenar los vértices en la GPU
        vertexBuffer = gpuCtx.device.createBuffer(
            vertices.size * 4,  // Tamaño en bytes
            GPUBufferUsage.VERTEX or GPUBufferUsage.COPY_DST,  // Uso como buffer de vértices y destino de copia
            false,  // No necesitamos mapearlo para escritura directa
            "vertexBuffer"
        )

        // Copiar los datos de los vértices al buffer
        gpuCtx.queue.writeBuffer(vertexBuffer!!, 0, vertices)


        val vertexBufferLayouts = arrayOf<GPUVertexBufferLayout>(
            GPUVertexBufferLayout(
                attributes = arrayOf(GPUVertexAttribute(
                    GPUVertexFormat.FLOAT32X2,
                    0,
                    0)
                ),
                arrayStride = 8
            )
        )

        val renderPipelineDescriptor = GPURenderPipelineDescriptor(
            layout = "auto",
            vertex = GPUVertexState(
                module = shaderModule,
                entryPoint = "vertexMain",
                buffers = vertexBufferLayouts
            ),
            fragment = GPUFragmentState(
                module = shaderModule,
                entryPoint = "fragmentMain",
                targets = arrayOf(
                    GPUColorTargetState(
                        format = gpuCtx.getPreferredCanvasFormat().toString()
                    )
                )
            ),
            primitive = GPUPrimitiveState(
                topology = GPUPrimitiveTopology.TRIANGLE_LIST,
                cullMode = GPUCullMode.BACK
            )
        )

        pipeline = gpuCtx.device.createRenderPipeline(renderPipelineDescriptor.toGpu())
    }

    actual fun unbind() {
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext
//        gpuCtx.submitCommands()
    }

    actual fun render() {
        renderCube()
    }

    fun renderTriangle() {
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext

        // Paso 12: Crear un encoder de comandos
        // El encoder nos permite crear comandos para la GPU
        val commandEncoder = gpuCtx.device.createCommandEncoder()
        
        // Paso 13: Obtener una textura para renderizar
        val textureView = gpuCtx.webGPUContext?.getCurrentTexture()?.createView()
        
        // Paso 14: Crear un pass de renderizado
        // Un pass de renderizado contiene los comandos para dibujar
        val renderPass = commandEncoder.beginRenderPass(
            object : GPURenderPassDescriptor {
                override var colorAttachments: Array<GPURenderPassColorAttachment> = arrayOf(
                    object : GPURenderPassColorAttachment {
                        override var view: GPUTextureView = textureView!!
                        override var clearValue: GPUColor = object : GPUColor {
                            override var r = 0.1f
                            override var g = 0.1f
                            override var b = 0.1f
                            override var a = 1.0f
                        }
                        override var loadOp = GPULoadOp.CLEAR
                        override var storeOp = GPUStoreOp.STORE
                    }
                )
            }
        )
        
        // Paso 15: Configurar el pipeline y los recursos para renderizar
        renderPass.setPipeline(pipeline!!)
        renderPass.setVertexBuffer(0, vertexBuffer!!)
        
        // Paso 16: Dibujar el triángulo
        // 3 vértices, 1 instancia, comenzando desde el vértice 0 y la instancia 0
        renderPass.draw(3, 1, 0, 0)
        
        // Paso 17: Finalizar el pass de renderizado
        renderPass.end()
        
        // Paso 18: Finalizar y enviar los comandos a la GPU
        val commandBuffer = commandEncoder.finish()
        gpuCtx.queue.submit(arrayOf(commandBuffer))
        //window.requestAnimationFrame { render() }
    }

    fun renderCube() {
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext
        if (uniformBuffer == null || pipeline == null) {
            return
        }
        // ctx.scene?.render(ctx)

        val mvp = ZMatrix4(arrayOf<Float>(1.0867713689804077f, -0.7829893231391907f, -0.6631385087966919f, -0.6624753475189209f, 0f, 2.1683130264282227f, -0.44014039635658264f, -0.43970024585723877f, -1.1871562004089355f, -0.7167804837226868f, -0.6070641279220581f, -0.6064570546150208f, -1.1871559619903564f, -0.7167804837226868f, 2.7076656818389893f, 2.8049581050872803f))

        gpuCtx.queue.writeBuffer(uniformBuffer!!, 0, mvp.floatArray);


        val commandEncoder = gpuCtx.device.createCommandEncoder();
        val textureView = gpuCtx.webGPUContext?.getCurrentTexture()?.createView();
        val depthView = depthTexture?.createView();

        val colorAttachment = object : GPURenderPassColorAttachment {
            override var view: GPUTextureView = textureView!!
            override var loadOp = "clear"
            override var clearValue: GPUColor = object : GPUColor {
                override var r: Float = 0.13f
                override var g: Float = 0.13f
                override var b: Float = 0.15f
                override var a: Float = 1.0f
            }
            override var storeOp = "store"
        }
        
        val depthAttachment = object : GPURenderPassDepthStencilAttachment {
            override var view = depthView!!
            override var depthLoadOp = "clear"
            override var depthClearValue = 1.0f
            override var depthStoreOp = "store"
        }
        
        val renderPassDescriptor = object : GPURenderPassDescriptor {
            override var colorAttachments: Array<GPURenderPassColorAttachment> = arrayOf(colorAttachment)
            override var depthStencilAttachment: GPURenderPassDepthStencilAttachment? = depthAttachment
        }

        val renderPass = commandEncoder.beginRenderPass(renderPassDescriptor)
        renderPass.setPipeline(pipeline!!)
        renderPass.setVertexBuffer(0, vertexBuffer!!)
        renderPass.setIndexBuffer(indexBuffer!!, "uint16")
        renderPass.setBindGroup(0, bindGroup!!)
        renderPass.drawIndexed(cubeIndices.size)
        renderPass.end()

        gpuCtx.queue.submit(arrayOf(commandEncoder.finish()))
        //window.requestAnimationFrame { render() };
    }

    actual override fun onViewportResize(width: Int, height: Int) {
        currentWidth = width
        currentHeight = height
        val gpuCtx = ctx.renderingContext as ZWebGPURenderingContext
//        gpuCtx.resizeCanvas(width, height)
    }
}