package zernikalos.context

import zernikalos.context.webgpu.*

class ZWebGPUDevice(val nativeDevice: GPUDevice) {

    fun createBuffer(byteSize: Int, usage: GPUBufferUsageFlags, mappedAtCreation: Boolean, label: String? = null): GPUBuffer {
        val descriptor = GPUBufferDescriptor(
            byteSize.toInt(),
            usage,
            mappedAtCreation,
            label
        )
        return nativeDevice.createBuffer(descriptor)
    }

    fun createShaderModule(source: String): GPUShaderModule {
        return nativeDevice.createShaderModule(GPUShaderModuleDescriptor(
            source,
             "ShaderModule"
        ))
    }

    fun createBindGroupLayout(descriptor: GPUBindGroupLayoutDescriptor): GPUBindGroupLayout {
        return nativeDevice.createBindGroupLayout(descriptor)
    }

    fun createBindGroup(descriptor: GPUBindGroupDescriptor): GPUBindGroup {
        return nativeDevice.createBindGroup(descriptor)
    }

    fun createPipelineLayout(descriptor: GPUPipelineLayoutDescriptor): GPUPipelineLayout {
        return nativeDevice.createPipelineLayout(descriptor)
    }

    fun createRenderPipeline(descriptor: GPURenderPipelineDescriptor): GPURenderPipeline {
        return nativeDevice.createRenderPipeline(descriptor)
    }

    fun createTexture(descriptor: GPUTextureDescriptor): GPUTexture {
        return nativeDevice.createTexture(descriptor)
    }

    fun createCommandEncoder(): GPUCommandEncoder {
        return nativeDevice.createCommandEncoder()
    }

    fun createSampler(descriptor: GPUSamplerDescriptor): GPUSampler {
        return nativeDevice.createSampler(descriptor)
    }

//    fun createCommandBuffer(): ZWebGPUCommandBuffer {
//        return ZWebGPUCommandBuffer(nativeDevice.createCommandEncoder())
//    }
//
//    fun createPipelineState(descriptor: ZPipelineDescriptor): ZPipelineState {
//        val pipeline = nativeDevice.createRenderPipeline(descriptor.toGPU())
//        return ZWebGPUTPipelineState(pipeline)
//    }
//
//    fun createBuffer(size: Long, usage: BufferUsage): ZBuffer {
//        val buffer = nativeDevice.createBuffer(
//            size = size,
//            usage = usage.toGPU(),
//            mappedAtCreation = false
//        )
//        return ZWebGPUBuffer(buffer)
//    }
//
//    fun createTexture(descriptor: ZTextureDescriptor): ZTexture {
//        val texture = nativeDevice.createTexture(descriptor.toGPU())
//        return ZWebGPUTexture(texture)
//    }
//
//    fun createShaderModule(source: String): ZShaderModule {
//        val shaderModule = nativeDevice.createShaderModule(
//            code = source
//        )
//        return ZWebGPUShaderModule(shaderModule)
//    }
}

// Extension functions para convertir entre tipos
//private fun BufferUsage.toGPU(): GPUBufferUsage {
//    return when (this) {
//        BufferUsage.VERTEX -> GPUBufferUsage.VERTEX
//        BufferUsage.INDEX -> GPUBufferUsage.INDEX
//        BufferUsage.UNIFORM -> GPUBufferUsage.UNIFORM
//        BufferUsage.STORAGE -> GPUBufferUsage.STORAGE
//        BufferUsage.COPY_SRC -> GPUBufferUsage.COPY_SRC
//        BufferUsage.COPY_DST -> GPUBufferUsage.COPY_DST
//    }
//}
//
//private fun ZPipelineDescriptor.toGPU(): GPURenderPipelineDescriptor {
//    return GPURenderPipelineDescriptor(
//        layout = layout,
//        vertex = vertexStage.toGPU(),
//        fragment = fragmentStage?.toGPU()
//    )
//}
//
//private fun ZShaderStage.toGPU(): GPUProgrammableStage {
//    return GPUProgrammableStage(
//        module = module,
//        entryPoint = entryPoint
//    )
//}
//
//private fun ZTextureDescriptor.toGPU(): GPUTextureDescriptor {
//    return GPUTextureDescriptor(
//        size = size,
//        format = format,
//        usage = usage
//    )
//}
