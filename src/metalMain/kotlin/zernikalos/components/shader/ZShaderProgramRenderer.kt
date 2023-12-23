package zernikalos.components.shader

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import platform.Foundation.NSError
import platform.Metal.*
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZShaderProgramRenderer actual constructor(ctx: ZRenderingContext, data: ZShaderProgramData) : ZComponentRender<ZShaderProgramData>(ctx, data) {

    var uniformBuffer: MTLBufferProtocol? = null

    lateinit var library: MTLLibraryProtocol
    lateinit var vertexShader: MTLFunctionProtocol
    lateinit var fragmentShader: MTLFunctionProtocol

    actual override fun initialize() {
        initializeShader()
        initializeUniformBuffer()
    }

    private fun initializeUniformBuffer() {
        ctx as ZMtlRenderingContext

        data.uniforms.values.forEach { uniform ->
            uniform.initialize(ctx)
        }

        val uniformsSize: Int = data.uniforms.values.fold(0) { acc, zUniform -> acc + zUniform.dataType.byteSize }

        uniformBuffer = ctx.device.newBufferWithLength(uniformsSize.toULong(), MTLResourceStorageModeShared)
        uniformBuffer?.label = "UniformBuffer"
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun initializeShader() {
        ctx as ZMtlRenderingContext

        var err: CPointer<ObjCObjectVar<NSError?>>? = null

        library = ctx.device.newLibraryWithSource(shaderSource, MTLCompileOptions(), err)!!

        vertexShader = library.newFunctionWithName("vertexShader")!!
        fragmentShader = library.newFunctionWithName("fragmentShader")!!
    }

    actual override fun bind() {
    }

    actual override fun unbind() {
    }

}

const val shaderSource = """
#include <metal_stdlib>
#include <simd/simd.h>

// Including header shared between this Metal shader code and Swift/C code executing Metal API commands
// #import "ShaderTypes.h"

using namespace metal;

typedef struct
{
    float3 position [[attribute(1)]];
    float3 color [[attribute(2)]];
    //float2 texCoord [[attribute(1)]];
} Vertex;

typedef struct
{
    float4 position [[position]];
    float3 color;
} ColorInOut;

typedef struct
{
    matrix_float4x4 mvpMatrix;
    // matrix_float4x4 mvpMatrix2;

//    matrix_float4x4 viewMatrix;
//    matrix_float4x4 modelMatrix;
} Uniforms;

vertex ColorInOut vertexShader(Vertex in [[stage_in]],
                               constant Uniforms & uniforms [[ buffer(7) ]])
{
    ColorInOut out;

    float4 position = float4(in.position, 1.0);
    out.position = uniforms.mvpMatrix * position;
    out.color = in.color;
    // out.texCoord = float2(1.0, 0.0);

    return out;
}

fragment float4 fragmentShader(ColorInOut in [[stage_in]],
                               constant Uniforms & uniforms [[ buffer(7) ]],
                               texture2d<half> colorMap     [[ texture(0) ]])
{
//    constexpr sampler colorSampler(mip_filter::linear,
//                                   mag_filter::linear,
//                                   min_filter::linear);
//
//    half4 colorSample   = colorMap.sample(colorSampler, in.texCoord.xy);

    return float4(in.color, 1.0);
}

"""