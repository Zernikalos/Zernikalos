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

        initializeAttributes()

        library = ctx.device.newLibraryWithSource(shaderSource, MTLCompileOptions(), err)!!

        vertexShader = library.newFunctionWithName("vertexShader")!!
        fragmentShader = library.newFunctionWithName("fragmentShader")!!
    }

    private fun initializeAttributes() {
        data.attributes.values.forEach {
            when (it.id) {
                1 -> shaderSource = "#define ATTR_POSITION\n$shaderSource"
                2 -> shaderSource = "#define ATTR_NORMAL\n$shaderSource"
                3 -> shaderSource = "#define ATTR_COLOR\n$shaderSource"
                4 -> shaderSource = "#define ATTR_UV\n$shaderSource"
            }
        }
    }

    actual override fun bind() {
    }

    actual override fun unbind() {
    }

}

var shaderSource = """
#include <metal_stdlib>
#include <simd/simd.h>

using namespace metal;

typedef struct
{
#ifdef ATTR_POSITION
    float3 position [[attribute(1)]];
#endif
#ifdef ATTR_COLOR
    float3 color [[attribute(3)]];
#endif
#ifdef ATTR_UV
    float2 texCoord [[attribute(4)]];
#endif
} Vertex;

typedef struct
{
#ifdef ATTR_POSITION
    float4 position [[position]];
#endif

    float3 color;

#ifdef ATTR_UV
    float2 texCoord;
#endif
} ColorInOut;

typedef struct
{
    matrix_float4x4 mvpMatrix;
    // matrix_float4x4 mvpMatrix2;

//    matrix_float4x4 viewMatrix;
//    matrix_float4x4 modelMatrix;
} Uniforms;

ColorInOut computeOutColor(Vertex in) {
    ColorInOut out;

#if defined(ATTR_UV)
    out.texCoord.x = in.texCoord.x;
    out.texCoord.y = 2 - in.texCoord.y;
#elif defined(ATTR_COLOR)
    out.color = in.color;
#else
    out.color = float2(1.0, 0.0);
#endif 
    
    return out;
}

vertex ColorInOut vertexShader(Vertex in [[stage_in]],
                               constant Uniforms & uniforms [[ buffer(7) ]])
{

    ColorInOut out = computeOutColor(in);

    float4 position = float4(in.position, 1.0);
    out.position = uniforms.mvpMatrix * position;

    return out;
}

#if defined(ATTR_UV)
float4 fragmentComputeColorOutFromTexture(ColorInOut in, texture2d<half> colorMap) {
    constexpr sampler colorSampler(mip_filter::linear,
                                   mag_filter::linear,
                                   min_filter::linear);

    half4 colorSample = colorMap.sample(colorSampler, in.texCoord.xy);

    return float4(colorSample);
}
#endif

float4 fragmentComputeColorOut(ColorInOut in) {
    return float4(in.color, 1.0);
}

fragment float4 fragmentShader(ColorInOut in [[stage_in]],
                               constant Uniforms & uniforms [[ buffer(7) ]],
                               texture2d<half> colorMap     [[ texture(0) ]])
{
#if defined(ATTR_UV)
    return fragmentComputeColorOutFromTexture(in, colorMap);
#else
    return fragmentComputeColorOut(in);
#endif
}

"""