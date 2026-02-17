/*
 *
 *  * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package zernikalos.context.webgpu

import kotlinx.browser.window
import org.w3c.dom.Navigator
import kotlin.js.Promise

val Navigator.gpu: GPU? get() = window.navigator.asDynamic().gpu

external class GPU {
    fun getPreferredCanvasFormat(): GPUTextureFormat
    fun requestAdapter(): Promise<GPUAdapter?>
}

external interface GPUCanvasConfiguration {
    var device: GPUDevice
    var format: GPUTextureFormat
    var usage: GPUTextureUsageFlags?
        get() = definedExternally
        set(value) = definedExternally
    var alphaMode: String?
        get() = definedExternally
        set(value) = definedExternally
    var viewFormats: Array<GPUTextureFormat>?
        get() = definedExternally
        set(value) = definedExternally
}

external class GPUCanvasContext {
    fun configure(descriptor: GPUCanvasConfiguration): GPUSwapChain
    fun getCurrentTexture(): GPUTexture
}

external class GPUDevice {
    val queue: GPUQueue
    fun createCommandEncoder(): GPUCommandEncoder
    fun createBuffer(descriptor: GPUBufferDescriptor): GPUBuffer
    fun createShaderModule(descriptor: GPUShaderModuleDescriptor): GPUShaderModule
    fun createRenderPipeline(descriptor: GPURenderPipelineDescriptor): GPURenderPipeline
    fun createBindGroupLayout(descriptor: GPUBindGroupLayoutDescriptor): GPUBindGroupLayout
    fun createBindGroup(descriptor: GPUBindGroupDescriptor): GPUBindGroup
    fun createPipelineLayout(descriptor: GPUPipelineLayoutDescriptor): GPUPipelineLayout
    fun createTexture(descriptor: GPUTextureDescriptor): GPUTexture
    fun createSampler(descriptor: GPUSamplerDescriptor): GPUSampler
}

external class GPUBindGroup

data class GPUBindGroupLayout(
    @JsName("label")
    var label: String? = "",
)

data class GPUBufferDescriptor(
    @JsName("size")
    val size: Int,
    @JsName("usage")
    val usage: GPUBufferUsageFlags,
    @JsName("mappedAtCreation")
    val mappedAtCreation: Boolean? = undefined,
    @JsName("label")
    val label: String? = undefined
)

data class GPUBindGroupDescriptor(
    @JsName("layout")
    var layout: GPUBindGroupLayout,
    @JsName("entries")
    var entries: Array<GPUBindGroupEntry>?,
    @JsName("label")
    var label: String? = ""
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["layout"] = layout
        if (entries != null) o["entries"] = entries!!.map { it.toGpu() }.toTypedArray()
        if (label != null) o["label"] = label
        return o
    }
}

data class GPUBindGroupEntry(
    @JsName("binding")
    var binding: Int,
    @JsName("resource")
    var resource: Any // GPUBufferBinding, GPUSampler, GPUTextureView, or GPUBindGroupResource for old code
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["binding"] = binding
        o["resource"] = when(resource) {
            is GPUTextureView -> resource
            is GPUBufferBinding -> (resource as GPUBufferBinding).toGpu()
            is GPUBindGroupResource -> (resource as GPUBindGroupResource).toGpu() // for backwards compatibility
            else -> resource // for GPUTextureView and GPUSampler
        }
        return o
    }
}

//external interface GPUBindGroupResource {
//    var buffer: GPUBuffer?
//}

data class GPUBindGroupResource(
    @JsName("buffer")
    var buffer: GPUBuffer
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["buffer"] = buffer
        return o
    }
}

data class GPUBufferBinding(
    var buffer: GPUBuffer,
    var offset: Long,
    var size: Long?
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["buffer"] = buffer
        o["offset"] = offset
        if (size != null) o["size"] = size
        return o
    }
}

data class GPUBindGroupLayoutDescriptor(
    @JsName("entries")
    var entries: Array<GPUBindGroupLayoutEntry>?,
    @JsName("label")
    var label: String?
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        if (entries != null) o["entries"] = entries!!.map { it.toGpu() }.toTypedArray()
        if (label != null) o["label"] = label
        return o
    }
}

data class GPUBindGroupLayoutEntry(
    @JsName("binding")
    var binding: Int,
    @JsName("visibility")
    var visibility: Int,
    @JsName("buffer")
    var buffer: GPUBufferBindingLayout? = undefined,
    @JsName("sampler")
    var sampler: GPUSamplerBindingLayout? = undefined,
    @JsName("texture")
    var texture: GPUTextureBindingLayout? = undefined
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["binding"] = binding
        o["visibility"] = visibility
        if (buffer != null) o["buffer"] = buffer?.toGpu()
        if (sampler != null) o["sampler"] = sampler?.toGpu()
        if (texture != null) o["texture"] = texture?.toGpu()
        return o
    }
}

data class GPUBufferBindingLayout(
    @JsName("type")
    var type: String?,
    @JsName("hasDynamicOffset")
    var hasDynamicOffset: Boolean? = null,
    @JsName("minBindingSize")
    var minBindingSize: Long? = null
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        if (type != null) o["type"] = type
        if (hasDynamicOffset != null) o["hasDynamicOffset"] = hasDynamicOffset
        if (minBindingSize != null) o["minBindingSize"] = minBindingSize
        return o
    }
}

data class GPUSamplerBindingLayout(
    @JsName("type")
    var type: String? = undefined // GPUSamplerBindingType
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        if (type != null) o["type"] = type
        return o
    }
}

data class GPUTextureBindingLayout(
    @JsName("sampleType")
    var sampleType: String? = undefined, // GPUTextureSampleType
    @JsName("viewDimension")
    var viewDimension: String? = undefined, // GPUTextureViewDimension
    @JsName("multisampled")
    var multisampled: Boolean? = undefined
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        if (sampleType != null) o["sampleType"] = sampleType
        if (viewDimension != null) o["viewDimension"] = viewDimension
        if (multisampled != null) o["multisampled"] = multisampled
        return o
    }
}

typealias GPUShaderStageFlags = Int

external object GPUShaderStage {
    val VERTEX: Int
    val FRAGMENT: Int
    val COMPUTE: Int
}

object GPUSamplerBindingType {
    const val FILTERING = "filtering"
    const val NON_FILTERING = "non-filtering"
    const val COMPARISON = "comparison"
}

object GPUTextureSampleType {
    const val FLOAT = "float"
    const val UNFILTERABLE_FLOAT = "unfilterable-float"
    const val DEPTH = "depth"
    const val SINT = "sint"
    const val UINT = "uint"
}

object GPUTextureViewDimension {
    const val D1 = "1d"
    const val D2 = "2d"
    const val D2_ARRAY = "2d-array"
    const val CUBE = "cube"
    const val CUBE_ARRAY = "cube-array"
    const val D3 = "3d"
}

typealias GPUBufferBindingTypeFlags = String

object GPUBufferBindingType {
    val UNIFORM = "uniform"
    val STORAGE = "storage"
    val READ_ONLY_STORAGE = "read-only-storage"
}

external interface GPUStorageTextureBindingLayout {
    var access: String?
    var format: String
    var viewDimension: String?
}

data class GPUShaderModuleDescriptor(
    @JsName("code")
    var code: String,
    @JsName("label")
    var label: String? = ""
)

data class GPURenderPipelineDescriptor(
    @JsName("layout")
    var layout: Any? = null,

    @JsName("vertex")
    var vertex: GPUVertexState,

    @JsName("fragment")
    var fragment: GPUFragmentState? = null,

    @JsName("primitive")
    var primitive: GPUPrimitiveState? = null,

    @JsName("depthStencil")
    var depthStencil: GPUDepthStencilState? = null,

    @JsName("multisample")
    var multisample: GPUMultisampleState? = null,

    @JsName("label")
    var label: String? = ""
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        if (layout != null) o["layout"] = layout
        o["vertex"] = vertex.toGpu()
        if (fragment != null) o["fragment"] = fragment!!.toGpu()
        if (primitive != null) o["primitive"] = primitive!!.toGpu()
        if (depthStencil != null) o["depthStencil"] = depthStencil!!.toGpu()
        if (multisample != null) o["multisample"] = multisample
        if (label != null) o["label"] = label
        return o
    }
}

data class GPUPipelineLayout(
    var bindGroupLayouts: Array<GPUBindGroupLayout>
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["bindGroupLayouts"] = bindGroupLayouts
        return o
    }
}

data class GPUPipelineLayoutDescriptor(
    @JsName("bindGroupLayouts")
    var bindGroupLayouts: Array<GPUBindGroupLayout>
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["bindGroupLayouts"] = bindGroupLayouts
        return o
    }
}

data class GPUVertexState(
    @JsName("module")
    var module: GPUShaderModule,
    @JsName("entryPoint")
    var entryPoint: String,
    @JsName("buffers")
    var buffers: Array<GPUVertexBufferLayout?>? = emptyArray<GPUVertexBufferLayout?>()
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["module"] = module
        o["entryPoint"] = entryPoint
        if (buffers != null) o["buffers"] = buffers!!.map { it?.toGpu() }.toTypedArray()
        return o
    }
}

data class GPUVertexBufferLayout(
    @JsName("arrayStride")
    var arrayStride: Int,
    @JsName("stepMode")
    var stepMode: String? = null,
    @JsName("attributes")
    var attributes: Array<GPUVertexAttribute>
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["arrayStride"] = arrayStride
        if (stepMode != null) o["stepMode"] = stepMode
        o["attributes"] = attributes.map { it.toGpu() }.toTypedArray()
        return o
    }
}

data class GPUVertexAttribute(
    @JsName("format")
    var format: String,
    @JsName("offset")
    var offset: Int,
    @JsName("shaderLocation")
    var shaderLocation: Int
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["format"] = format
        o["offset"] = offset
        o["shaderLocation"] = shaderLocation
        return o
    }
}

data class GPUFragmentState(
    @JsName("module")
    var module: GPUShaderModule,
    @JsName("entryPoint")
    var entryPoint: String,
    @JsName("targets")
    var targets: Array<GPUColorTargetState>
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["module"] = module
        o["entryPoint"] = entryPoint
        o["targets"] = targets.map { it.toGpu() }.toTypedArray()
        return o
    }
}

data class GPUColorTargetState(
    @JsName("format")
    var format: String,
    @JsName("blend")
    var blend: GPUBlendState? = null,
    @JsName("writeMask")
    var writeMask: Int? = null
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["format"] = format
        if (blend != null) o["blend"] = blend
        if (writeMask != null) o["writeMask"] = writeMask
        return o
    }
}

external interface GPUBlendState {
    var color: GPUBlendComponent
    var alpha: GPUBlendComponent
}

external interface GPUBlendComponent {
    var operation: String
    var srcFactor: String
    var dstFactor: String
}

object GPUPrimitiveTopology {
    const val POINT_LIST = "point-list"
    const val LINE_LIST = "line-list"
    const val LINE_STRIP = "line-strip"
    const val TRIANGLE_LIST = "triangle-list"
    const val TRIANGLE_STRIP = "triangle-strip"
}

object GPUCullMode {
    const val NONE = "none"
    const val FRONT = "front"
    const val BACK = "back"
}

data class GPUPrimitiveState(
    var topology: String,
    var stripIndexFormat: String? = null,
    var frontFace: String? = null,
    var cullMode: String? = null
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["topology"] = topology
        if (stripIndexFormat != null) o["stripIndexFormat"] = stripIndexFormat
        if (frontFace != null) o["frontFace"] = frontFace
        if (cullMode != null) o["cullMode"] = cullMode
        return o
    }
}

data class GPUDepthStencilState(
    var format: String,
    var depthWriteEnabled: Boolean? = null,
    var depthCompare: String? = null,
    var stencilFront: GPUStencilFaceState? = null,
    var stencilBack: GPUStencilFaceState? = null,
    var stencilReadMask: Int? = null,
    var stencilWriteMask: Int? = null,
    var depthBias: Int? = null,
    var depthBiasSlopeScale: Float? = null,
    var depthBiasClamp: Float? = null
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["format"] = format
        if (depthWriteEnabled != null) o["depthWriteEnabled"] = depthWriteEnabled
        if (depthCompare != null) o["depthCompare"] = depthCompare
        if (stencilFront != null) o["stencilFront"] = stencilFront
        if (stencilBack != null) o["stencilBack"] = stencilBack
        if (stencilReadMask != null) o["stencilReadMask"] = stencilReadMask
        if (stencilWriteMask != null) o["stencilWriteMask"] = stencilWriteMask
        if (depthBias != null) o["depthBias"] = depthBias
        if (depthBiasSlopeScale != null) o["depthBiasSlopeScale"] = depthBiasSlopeScale
        if (depthBiasClamp != null) o["depthBiasClamp"] = depthBiasClamp
        return o
    }
}


external interface GPUStencilFaceState {
    var compare: String?
    var failOp: String?
    var depthFailOp: String?
    var passOp: String?
}

external interface GPUMultisampleState {
    var count: Int?
    var mask: Int?
    var alphaToCoverageEnabled: Boolean?
}

external class GPURenderPipeline

external class GPUBuffer

external class GPUQueue {
    fun writeBuffer(buffer: GPUBuffer, offset: Int, data: Any)
    fun submit(commandBuffers: Array<GPUCommandBuffer>)
    fun copyExternalImageToTexture(source: GPUImageCopyExternalImage, destination: GPUImageCopyTexture, copySize: GPUExtent3D)
}

/**
 * Can be one of the following:
 * - ImageBitmap
 * - ImageData
 * - HTMLImageElement
 * - HTMLVideoElement
 * - VideoFrame
 * - HTMLCanvasElement
 * - OffscreenCanvas
 */
//typealias GPUImageCopyExternalImageSource = dynamic

data class GPUImageCopyExternalImage(
    @JsName("source")
    val source: dynamic,
    @JsName("origin")
    var origin: GPUOrigin2D? = undefined,
    @JsName("flipY")
    var flipY: Boolean? = undefined
)

data class GPUOrigin2D(
    @JsName("x")
    var x: Int? = undefined,
    @JsName("y")
    var y: Int? = undefined
)

data class GPUImageCopyTexture(
    @JsName("texture")
    val texture: GPUTexture
)

external class GPUShaderModule {

}

external class GPUSwapChain

external interface GPUTexture {
    fun createView(): GPUTextureView
}


external class GPUTextureView

data class GPURenderPassDescriptor(
    @JsName("colorAttachments")
    var colorAttachments: Array<GPURenderPassColorAttachment>,
    @JsName("depthStencilAttachment")
    var depthStencilAttachment: GPURenderPassDepthStencilAttachment? = null
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["colorAttachments"] = colorAttachments.map { it.toGpu() }.toTypedArray()
        if (depthStencilAttachment != null) o["depthStencilAttachment"] = depthStencilAttachment!!.toGpu()
        return o
    }
}
data class GPURenderPassColorAttachment(
    @JsName("view")
    var view: GPUTextureView,
    @JsName("loadOp")
    var loadOp: String,
    @JsName("storeOp")
    var storeOp: String,
    @JsName("clearValue")
    var clearValue: GPUColor
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["view"] = view
        o["loadOp"] = loadOp
        o["storeOp"] = storeOp
        o["clearValue"] = clearValue.toGpu()
        return o
    }
}
data class GPURenderPassDepthStencilAttachment(
    @JsName("view")
    var view: GPUTextureView,
    @JsName("depthLoadOp")
    var depthLoadOp: String,
    @JsName("depthStoreOp")
    var depthStoreOp: String,
    @JsName("depthClearValue")
    var depthClearValue: Float
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["view"] = view
        o["depthLoadOp"] = depthLoadOp
        o["depthStoreOp"] = depthStoreOp
        o["depthClearValue"] = depthClearValue
        return o
    }
}
external interface GPUSampler

object GPUAddressMode {
    const val CLAMP_TO_EDGE = "clamp-to-edge"
    const val REPEAT = "repeat"
    const val MIRROR_REPEAT = "mirror-repeat"
}

object GPUFilterMode {
    const val NEAREST = "nearest"
    const val LINEAR = "linear"
}

object GPUMipmapFilterMode {
    const val NEAREST = "nearest"
    const val LINEAR = "linear"
}

data class GPUSamplerDescriptor(
    @JsName("addressModeU")
    var addressModeU: String? = undefined, // GPUAddressMode
    @JsName("addressModeV")
    var addressModeV: String? = undefined, // GPUAddressMode
    @JsName("addressModeW")
    var addressModeW: String? = undefined, // GPUAddressMode
    @JsName("magFilter")
    var magFilter: String? = undefined, // GPUFilterMode
    @JsName("minFilter")
    var minFilter: String? = undefined, // GPUFilterMode
    @JsName("mipmapFilter")
    var mipmapFilter: String? = undefined, // GPUMipmapFilterMode
    @JsName("lodMinClamp")
    var lodMinClamp: Float? = undefined,
    @JsName("lodMaxClamp")
    var lodMaxClamp: Float? = undefined,
    @JsName("compare")
    var compare: String? = undefined, // GPUCompareFunction
    @JsName("maxAnisotropy")
    var maxAnisotropy: Short? = undefined,
    @JsName("label")
    var label: String? = undefined
)
external interface GPUCommandEncoder {
    fun beginRenderPass(descriptor: GPURenderPassDescriptor): GPURenderPassEncoder
    fun finish(): GPUCommandBuffer
}
external interface GPUCommandBuffer
external interface GPURenderPassEncoder {
    fun draw(vertexCount: Int, instanceCount: Int, firstVertex: Int, firstInstance: Int)
    fun setPipeline(pipeline: GPURenderPipeline)
    fun setVertexBuffer(slot: Int, buffer: GPUBuffer)
    fun setIndexBuffer(buffer: GPUBuffer, indexFormat: String)
    fun setBindGroup(slot: Int, bindGroup: GPUBindGroup)
    fun drawIndexed(indexCount: Int)
    fun end()
}
external interface GPUAdapter {
    fun requestDevice(): Promise<GPUDevice>
}
data class GPUExtent3D(
    @JsName("width")
    var width: Int,
    @JsName("height")
    var height: Int,
    @JsName("depth")
    var depth: Int? = undefined
) {
    fun toGpu(): dynamic {
        return arrayOf(width, height)
    }
}


data class GPUTextureDescriptor(
    @JsName("size")
    var size: GPUExtent3D,
    @JsName("format")
    var format: GPUTextureFormatFlags,
    @JsName("usage")
    var usage: GPUTextureUsageFlags,
    @JsName("sampleCount")
    var sampleCount: Int? = undefined,
    @JsName("dimension")
    var dimension: String? = undefined,
    @JsName("label")
    var label: String? = undefined
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["size"] = size.toGpu()
        o["format"] = format
        o["usage"] = usage
        if (sampleCount != null) o["sampleCount"] = sampleCount
        if (dimension != null) o["dimension"] = dimension
        if (label != null) o["label"] = label
        return o
    }
}

typealias GPUBufferUsageFlags = Int

external object GPUBufferUsage {
    val INDEX: Int
    val VERTEX: Int
    val UNIFORM: Int
    val COPY_DST: Int
    val COPY_SRC: Int
    val MAP_WRITE: Int
    val MAP_READ: Int
}

typealias GPUTextureFormatFlags = String

object GPUTextureFormat {
    const val R8Unorm = "r8unorm"
    const val R8Snorm = "r8snorm"
    const val R8Uint = "r8uint"
    const val R8Sint = "r8sint"
    const val R16Uint = "r16uint"
    const val R16Sint = "r16sint"
    const val R16Float = "r16float"
    const val RG8Unorm = "rg8unorm"
    const val RG8Snorm = "rg8snorm"
    const val RG8Uint = "rg8uint"
    const val RG8Sint = "rg8sint"
    const val R32Float = "r32float"
    const val R32Uint = "r32uint"
    const val R32Sint = "r32sint"
    const val RG16Uint = "rg16uint"
    const val RG16Sint = "rg16sint"
    const val RG16Float = "rg16float"
    const val RGBA8Unorm = "rgba8unorm"
    const val RGBA8UnormSRGB = "rgba8unorm-srgb"
    const val RGBA8Snorm = "rgba8snorm"
    const val RGBA8Uint = "rgba8uint"
    const val RGBA8Sint = "rgba8sint"
    const val BGRA8Unorm = "bgra8unorm"
    const val BGRA8UnormSRGB = "bgra8unorm-srgb"
    const val RGB10A2Unorm = "rgb10a2unorm"
    const val RG11B10Float = "rg11b10float"
    const val RG32Float = "rg32float"
    const val RG32Uint = "rg32uint"
    const val RG32Sint = "rg32sint"
    const val RGBA16Uint = "rgba16uint"
    const val RGBA16Sint = "rgba16sint"
    const val RGBA16Float = "rgba16float"
    const val RGBA32Float = "rgba32float"
    const val RGBA32Uint = "rgba32uint"
    const val RGBA32Sint = "rgba32sint"
    const val Stencil8 = "stencil8"
    const val Depth16Unorm = "depth16unorm"
    const val Depth24Plus = "depth24plus"
    const val Depth24PlusStencil8 = "depth24plus-stencil8"
    const val Depth32Float = "depth32float"
}

typealias GPUTextureUsageFlags = Int

external object GPUTextureUsage {
    val COPY_SRC: Int
    val COPY_DST: Int
    val TEXTURE_BINDING: Int
    val STORAGE_BINDING: Int
    val RENDER_ATTACHMENT: Int
}
external object GPUTextureDimension {
    val D1: String
    val D2: String
    val D3: String
}

data class GPUColor(
    @JsName("r")
    var r: Float,
    @JsName("g")
    var g: Float,
    @JsName("b")
    var b: Float,
    @JsName("a")
    var a: Float
) {
    fun toGpu(): dynamic {
        val o = js("{}")
        o["r"] = r
        o["g"] = g
        o["b"] = b
        o["a"] = a
        return o
    }
}

object GPULoadOp {
    const val LOAD = "load"
    const val CLEAR = "clear"
}

typealias GPUVertexFormatFlags = String

object GPUVertexFormat {
    // 8-bit formats
    val UINT8 = "uint8"
    val UINT8X2 = "uint8x2"
    val UINT8X4 = "uint8x4"
    val SINT8 = "sint8"
    val SINT8X2 = "sint8x2"
    val SINT8X4 = "sint8x4"
    val UNORM8 = "unorm8"
    val UNORM8X2 = "unorm8x2"
    val UNORM8X4 = "unorm8x4"
    val SNORM8 = "snorm8"
    val SNORM8X2 = "snorm8x2"
    val SNORM8X4 = "snorm8x4"

    // 16-bit formats
    val UINT16 = "uint16"
    val UINT16X2 = "uint16x2"
    val UINT16X4 = "uint16x4"
    val SINT16 = "sint16"
    val SINT16X2 = "sint16x2"
    val SINT16X4 = "sint16x4"
    val UNORM16 = "unorm16"
    val UNORM16X2 = "unorm16x2"
    val UNORM16X4 = "unorm16x4"
    val SNORM16 = "snorm16"
    val SNORM16X2 = "snorm16x2"
    val SNORM16X4 = "snorm16x4"
    val FLOAT16 = "float16"
    val FLOAT16X2 = "float16x2"
    val FLOAT16X4 = "float16x4"

    // 32-bit formats
    val FLOAT32 = "float32"
    val FLOAT32X2 = "float32x2"
    val FLOAT32X3 = "float32x3"
    val FLOAT32X4 = "float32x4"
    val UINT32 = "uint32"
    val UINT32X2 = "uint32x2"
    val UINT32X3 = "uint32x3"
    val UINT32X4 = "uint32x4"
    val SINT32 = "sint32"
    val SINT32X2 = "sint32x2"
    val SINT32X3 = "sint32x3"
    val SINT32X4 = "sint32x4"

    // 64-bit formats (requires "vertex-attribute-64bit" feature)
    val FLOAT64 = "float64"
    val FLOAT64X2 = "float64x2"
    val FLOAT64X3 = "float64x3"
    val FLOAT64X4 = "float64x4"

    // Packed formats
    val UNORM10_10_10_2 = "unorm10-10-10-2"
    val UNORM8X4_BGRA = "unorm8x4-bgra"
}

typealias GPUVertexStepModeFlags = String

object GPUVertexStepMode {
    val VERTEX = "vertex"
    val INSTANCE = "instance"
}

object GPUStoreOp {
    const val STORE = "store"
    const val DISCARD = "discard"
}

object GPUCompareFunction {
    const val NEVER = "never"
    const val LESS = "less"
    const val EQUAL = "equal"
    const val LESS_EQUAL = "less-equal"
    const val GREATER = "greater"
    const val NOT_EQUAL = "not-equal"
    const val GREATER_EQUAL = "greater-equal"
    const val ALWAYS = "always"
}
