/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator.libs

import zernikalos.components.shader.UNIFORM_IDS
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

#ifdef USE_PBR_MATERIAL
typedef struct
{
    float4 color;
    float4 emissive;
    float emissiveIntensity;
    float metalness;
    float roughness;
} PBRMaterialUniforms;
#endif

#ifdef USE_PHONG_MATERIAL
typedef struct
{
    float4 ambient;
    float4 diffuse;
    float4 specular;
    float shininess;
} PhongMaterialUniforms;
#endif

typedef struct
{
    matrix_float4x4 bones[100];
    matrix_float4x4 invBindMatrix[100];
} SkinningUniforms;
"""

val shaderVertexDefinitions = """
typedef struct
{
    #ifdef USE_POSITION
        float3 position [[attribute(${ZAttributeId.POSITION.id})]];
    #endif
    #ifdef USE_NORMALS
        float3 normal [[attribute(${ZAttributeId.NORMAL.id})]];
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

val shaderVertexMain = """
// Forward declaration
ColorInOut computeOutColor(Vertex in, constant Uniforms &uniforms, float4 finalPosition, float3 normal);

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
            // The skinning matrix is the product of the bone's current transform
            // and its inverse bind matrix.
            matrix_float4x4 skinMatrix = skinUniforms.bones[boneID] * skinUniforms.invBindMatrix[boneID];
            skinnedPosition += skinMatrix * float4(basePosition, 1.0) * boneWeights[i];
            totalWeight += boneWeights[i];
        }
    }
    // Normalize the position by the total weight to average the influence
    return totalWeight > 0.0 ? skinnedPosition / totalWeight : float4(basePosition, 1.0);
}
#endif

vertex ColorInOut vertexShader(Vertex in [[stage_in]],
                               constant Uniforms &uniforms [[buffer(${UNIFORM_IDS.BLOCK_SCENE_MATRIX})]]
                               #ifdef USE_SKINNING
                               , constant SkinningUniforms &skinUniforms [[buffer(${UNIFORM_IDS.BLOCK_SKINNING_MATRIX})]]
                               #endif
                              )
{
    float4 finalPosition;
    float3 finalNormal;

    #ifdef USE_SKINNING
        finalPosition = calculateSkinnedPosition(in.position, in.boneIndices, in.boneWeights, skinUniforms);
        // For normals, we'd typically transform them with the inverse transpose of the bone matrix.
        // This is complex to do per-vertex. A common simplification is to use the model-view matrix,
        // but for skinned meshes, a more accurate approach involves the skinning matrix without translation.
        // For now, let's stick to a simpler transformation that works for non-uniform scaling.
        finalNormal = normalize((uniforms.viewMatrix * float4(in.normal, 0.0)).xyz);
    #else
        finalPosition = float4(in.position, 1.0);
        finalNormal = normalize((uniforms.viewMatrix * float4(in.normal, 0.0)).xyz);
    #endif

    ColorInOut out = computeOutColor(in, uniforms, finalPosition, finalNormal);
    out.position = uniforms.mvpMatrix * finalPosition;

    out.viewPosition = (uniforms.viewMatrix * finalPosition).xyz;

    return out;
}

ColorInOut computeOutColor(Vertex in, constant Uniforms &uniforms, float4 finalPosition, float3 normal) {
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

#ifdef USE_NORMALS
    out.normal = normal;
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

    #ifdef USE_NORMALS
        float3 normal;
    #endif

    float3 viewPosition;
    float3 color;

    #ifdef USE_TEXTURE
        float2 texCoord;
    #endif
} ColorInOut;
"""

val shaderFragmentMain = """

// Basic PBR calculation constants
constant float PI = 3.14159265359;

#if defined(USE_PBR_MATERIAL) && defined(USE_NORMALS)
// Calculates the distribution of microfacets using the Trowbridge-Reitz GGX formula.
float DistributionGGX(float3 N, float3 H, float roughness) {
    float a = roughness * roughness;
    float a2 = a * a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH * NdotH;

    float nom   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;
    return nom / denom;
}

// Calculates the geometric obstruction of microfacets.
float GeometrySchlickGGX(float NdotV, float roughness) {
    float r = (roughness + 1.0);
    float k = (r * r) / 8.0;
    float nom = NdotV;
    float denom = NdotV * (1.0 - k) + k;
    return nom / denom;
}

// Calculates the geometry factor for both direct and view vectors.
float GeometrySmith(float3 N, float3 V, float3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = GeometrySchlickGGX(NdotV, roughness);
    float ggx1 = GeometrySchlickGGX(NdotL, roughness);
    return ggx1 * ggx2;
}

// Calculates the Fresnel effect, which describes the ratio of reflected light.
float3 fresnelSchlick(float cosTheta, float3 F0) {
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

float3 calculatePBRColor(
    float4 baseColor,
    float3 normal,
    float3 viewPosition,
    constant PBRMaterialUniforms &pbrMaterial
) {
    // Basic lighting properties (hardcoded for now)
    float3 lightPos = float3(5.0, 5.0, 5.0);
    float3 lightColor = float3(1.0, 1.0, 1.0) * 2.0; // Light intensity

    // Material properties from uniform
    float3 albedo = pbrMaterial.color.rgb * baseColor.rgb;
    float metalness = pbrMaterial.metalness;
    float roughness = pbrMaterial.roughness;

    // Vector calculations
    float3 N = normalize(normal);
    float3 V = normalize(-viewPosition);
    float3 L = normalize(lightPos); // For a directional light
    float3 H = normalize(V + L);

    // Fresnel at normal incidence (F0)
    float3 F0 = float3(0.04);
    F0 = mix(F0, albedo, metalness);

    // Cook-Torrance BRDF
    float NDF = DistributionGGX(N, H, roughness);
    float G = GeometrySmith(N, V, L, roughness);
    float3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

    float3 kS = F;
    float3 kD = float3(1.0) - kS;
    kD *= 1.0 - metalness;

    float3 numerator = NDF * G * F;
    float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001; // Add epsilon
    float3 specular = numerator / denominator;

    // Add light contribution
    float NdotL = max(dot(N, L), 0.0);
    float3 Lo = (kD * albedo / PI + specular) * lightColor * NdotL;

    // Ambient light
    float3 ambient = float3(0.03) * albedo;
    float3 color = ambient + Lo;

    return color;
}
#endif

#if defined(USE_PHONG_MATERIAL) && defined(USE_NORMALS)
// Blinn-Phong lighting calculation
float3 calculateBlinnPhongColor(
    float4 baseColor,
    float3 normal,
    float3 viewPosition,
    constant PhongMaterialUniforms &phongMaterial
) {
    // Basic lighting properties (hardcoded for now)
    float3 lightPos = float3(5.0, -5.0, 5.0);
    float3 lightColor = float3(1.0, 1.0, 1.0) * 2.0; // Light intensity

    // Material properties from uniform
    float3 ambient = phongMaterial.ambient.rgb;
    float3 diffuse = phongMaterial.diffuse.rgb * baseColor.rgb;
    float3 specular = phongMaterial.specular.rgb;
    float shininess = phongMaterial.shininess;

    // Vector calculations
    float3 N = normalize(normal);
    float3 V = normalize(-viewPosition); // View direction
    float3 L = normalize(lightPos); // For a directional light
    float3 H = normalize(V + L); // Halfway vector for Blinn-Phong

    // Ambient component
    float3 ambientComponent = ambient * baseColor.rgb;

    // Diffuse component
    float NdotL = max(dot(N, L), 0.0);
    float3 diffuseComponent = diffuse * lightColor * NdotL;

    // Specular component (Blinn-Phong)
    float NdotH = max(dot(N, H), 0.0);
    float3 specularComponent = specular * lightColor * pow(NdotH, shininess);

    // Combine all components
    float3 finalColor = ambientComponent + diffuseComponent + specularComponent;
    
    return finalColor;
}
#endif

#if defined(USE_TEXTURE)
    float4 fragmentComputeColorOutFromTexture(ColorInOut in, texture2d<half> colorMap, sampler colorSampler) {
        half4 colorSample = colorMap.sample(colorSampler, in.texCoord.xy);

        return float4(colorSample);
    }
#endif

float4 fragmentComputeColorOut(ColorInOut in) {
    return float4(in.color, 1.0);
}

fragment float4 fragmentShader(ColorInOut in [[stage_in]],
                               constant Uniforms & uniforms [[ buffer(${UNIFORM_IDS.BLOCK_SCENE_MATRIX}) ]]
                               #if defined(USE_PBR_MATERIAL)
                               , constant PBRMaterialUniforms &pbrMaterial [[buffer(${UNIFORM_IDS.BLOCK_PBR_MATERIAL})]]
                               #endif
                               #if defined(USE_PHONG_MATERIAL)
                               , constant PhongMaterialUniforms &phongMaterial [[buffer(${UNIFORM_IDS.BLOCK_PHONG_MATERIAL})]]
                               #endif
                               #if defined(USE_TEXTURE)
                               , texture2d<half> colorMap     [[ texture(0) ]]
                               , sampler colorSampler         [[ sampler(0) ]]
                               #endif
                               )
{
    float4 baseColor = float4(1.0);

    #if defined(USE_TEXTURE)
        baseColor = fragmentComputeColorOutFromTexture(in, colorMap, colorSampler);
    #elif defined(USE_COLOR)
        baseColor = float4(in.color, 1.0);
    #endif

    #if defined(USE_PBR_MATERIAL)
        float3 emissive = pbrMaterial.emissive.rgb * pbrMaterial.emissiveIntensity;
        #if defined(USE_NORMALS)
            float3 pbrColor = calculatePBRColor(baseColor, in.normal, in.viewPosition, pbrMaterial);
            float3 finalColor = pbrColor + emissive;
        #else
            float3 finalColor = baseColor.rgb + emissive;
        #endif

        // Tonemapping (ACES approximation) and gamma correction
        finalColor = finalColor / (finalColor + float3(1.0));
        finalColor = pow(finalColor, float3(1.0/2.2));
        return float4(finalColor, baseColor.a);
    #elif defined(USE_PHONG_MATERIAL)
        #if defined(USE_NORMALS)
            float3 phongColor = calculateBlinnPhongColor(baseColor, in.normal, in.viewPosition, phongMaterial);
            return float4(phongColor, baseColor.a);
        #else
            return baseColor;
        #endif
    #else
        return baseColor;
    #endif
}
"""
