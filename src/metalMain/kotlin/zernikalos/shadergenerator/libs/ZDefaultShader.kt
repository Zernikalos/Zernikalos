package zernikalos.shadergenerator.libs

const val shaderCommonHeaders = """
#include <metal_stdlib>
#include <simd/simd.h>

using namespace metal;
"""

const val shaderUniforms = """
typedef struct
{
    matrix_float4x4 mvpMatrix;
    // matrix_float4x4 mvpMatrix2;

    //    matrix_float4x4 viewMatrix;
    //    matrix_float4x4 modelMatrix;
} Uniforms;
"""

const val shaderVertexDefinitions = """
typedef struct
{
    #ifdef USE_POSITION
        float3 position [[attribute(1)]];
    #endif
    #ifdef USE_COLOR
        float3 color [[attribute(3)]];
    #endif
    #ifdef USE_TEXTURE
        float2 texCoord [[attribute(4)]];
    #endif
} Vertex;
"""

const val shaderVertexMain = """
ColorInOut computeOutColor(Vertex in) {
    ColorInOut out;

    #if defined(USE_TEXTURE)
        out.texCoord.x = in.texCoord.x;
        out.texCoord.y = in.texCoord.y;
    #elif defined(USE_COLOR)
        out.color = in.color;
    #else
        out.color = float3(1.0, 0.0, 0.0);
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
"""

const val shaderFragmentDefinitions = """
typedef struct
{
    #ifdef USE_POSITION
        float4 position [[position]];
    #endif

    float3 color;

    #ifdef USE_TEXTURE
        float2 texCoord;
    #endif
} ColorInOut;
"""

const val shaderFragmentMain = """
#if defined(USE_TEXTURE)
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
    #if defined(USE_TEXTURE)
        return fragmentComputeColorOutFromTexture(in, colorMap);
    #else
        return fragmentComputeColorOut(in);
    #endif
}
"""