/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator.libs

import zernikalos.components.shader.ZAttributeId

const val shaderCommonHeaders = """
#include <metal_stdlib>
#include <simd/simd.h>

using namespace metal;
"""

const val shaderUniforms = """
typedef struct
{
    matrix_float4x4 projectionMatrix;
    matrix_float4x4 viewMatrix;
    matrix_float4x4 mvpMatrix;
} Uniforms;

typedef struct
{
    matrix_float4x4 bones[100];
    matrix_float4x4 invBindMatrix[100];;
} SkinningUniforms;
"""

val shaderVertexDefinitions = """
typedef struct
{
    #ifdef USE_POSITION
        float3 position [[attribute(${ZAttributeId.POSITION.id})]];
    #endif
    #ifdef USE_COLOR
        float3 color [[attribute(${ZAttributeId.COLOR.id})]];
    #endif
    #ifdef USE_TEXTURE
        float2 texCoord [[attribute(${ZAttributeId.UV.id})]];
    #endif
    #ifdef USE_SKINNING
        float4 boneWeights [[attribute(${ZAttributeId.BONE_WEIGHT.id})]];
        float4 boneIndices [[attribute(${ZAttributeId.BONE_INDEX.id})]];
    #endif
} Vertex;
"""

const val shaderVertexMain = """
ColorInOut computeOutColor(Vertex in) {
    ColorInOut out;

    #if defined(USE_TEXTURE)
        #if defined(FLIP_TEXTURE_Y)
            out.texCoord.x = in.texCoord.x;
            out.texCoord.y = 1.0 - in.texCoord.y;
        #else
            out.texCoord.x = in.texCoord.x;
            out.texCoord.y = in.texCoord.y;
        #endif
    #elif defined(USE_COLOR)
        out.color = in.color;
    #else
        out.color = float3(1.0, 1.0, 1.0); // Default to white if no color/texture
    #endif

    return out;
}

#ifdef USE_SKINNING
float4 calculateSkinnedPosition(
    float3 basePosition,
    float4 boneIndices,
    float4 boneWeights,
    constant SkinningUniforms &skinUniforms
) {
    float4 skinnedPosition = float4(0.0);
    float totalWeight = 0.0;

    for (int i = 0; i < 4; ++i) {
        if (boneWeights[i] > 0.0) {
            int boneID = int(boneIndices[i]);
            // It's good practice to ensure boneID is within valid range if possible,
            // but for a direct port, we'll assume valid indices as in GLSL.
            matrix_float4x4 skinMatrix = skinUniforms.bones[boneID] * skinUniforms.invBindMatrix[boneID];
            float4 posedPosition = skinMatrix * float4(basePosition, 1.0);

            skinnedPosition += boneWeights[i] * posedPosition;
            totalWeight += boneWeights[i];
        }
    }

    if (totalWeight > 0.0) {
        return skinnedPosition / totalWeight;
    } else {
        return float4(basePosition, 1.0);
    }
}
#endif

vertex ColorInOut vertexShader(Vertex in [[stage_in]],
                               constant Uniforms &uniforms [[buffer(10)]]
                               #ifdef USE_SKINNING
                               , constant SkinningUniforms &skinUniforms [[buffer(11)]]
                               #endif
                              )
{
    ColorInOut out = computeOutColor(in);

    #ifdef USE_SKINNING
        float4 finalPosition = calculateSkinnedPosition(in.position, in.boneIndices, in.boneWeights, skinUniforms);
        out.position = uniforms.mvpMatrix * finalPosition;
    #else
        float4 position = float4(in.position, 1.0);
        out.position = uniforms.mvpMatrix * position;
    #endif

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
                                       min_filter::linear,
                                       address::repeat);

        half4 colorSample = colorMap.sample(colorSampler, in.texCoord.xy);

        return float4(colorSample);
    }
#endif

float4 fragmentComputeColorOut(ColorInOut in) {
    return float4(in.color, 1.0);
}

fragment float4 fragmentShader(ColorInOut in [[stage_in]],
                               constant Uniforms & uniforms [[ buffer(10) ]],
                               texture2d<half> colorMap     [[ texture(0) ]])
{
    #if defined(USE_TEXTURE)
        return fragmentComputeColorOutFromTexture(in, colorMap);
    #else
        return fragmentComputeColorOut(in);
    #endif
}
"""
