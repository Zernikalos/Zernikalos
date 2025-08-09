/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator.libs

import zernikalos.components.shader.UNIFORM_IDS
import zernikalos.components.shader.ZAttributeId

val skinningShaderSource = """
const PI = 3.14159265359;

// Uniforms
struct SceneUniforms {
    projectionMatrix : mat4x4<f32>,
    viewMatrix : mat4x4<f32>,
    modelViewProjectionMatrix : mat4x4<f32>
}
@binding(${UNIFORM_IDS.BLOCK_SCENE_MATRIX}) @group(0) var<uniform> sceneUniforms : SceneUniforms;

struct SkinningUniforms {
    bones : array<mat4x4<f32>, 100>,
    invBindMatrix : array<mat4x4<f32>, 100>
}
@binding(${UNIFORM_IDS.BLOCK_SKINNING_MATRIX}) @group(0) var<uniform> skinUniforms : SkinningUniforms;

struct PBRMaterialUniforms {
    color: vec4<f32>,
    emissive: vec4<f32>,
    emissiveIntensity: f32,
    metalness: f32,
    roughness: f32
}
@binding(${UNIFORM_IDS.BLOCK_PBR_MATERIAL}) @group(0) var<uniform> pbrMaterial: PBRMaterialUniforms;

// Bindings
@group(1) @binding(0) var t_diffuse: texture_2d<f32>;
@group(1) @binding(1) var s_diffuse: sampler;

// Vertex Data
struct VertexInput {
    @location(${ZAttributeId.POSITION.id}) position: vec3<f32>,
    @location(${ZAttributeId.NORMAL.id}) normal: vec3<f32>,
    @location(${ZAttributeId.UV.id}) uv : vec2<f32>,
    // @location(${ZAttributeId.COLOR.id}) color : vec3<f32>,
    @location(${ZAttributeId.BONE_WEIGHT.id}) boneWeights : vec4<f32>,
    @location(${ZAttributeId.BONE_INDEX.id}) boneIndices : vec4<u32>,
}

struct VertexOutput {
    @builtin(position) position : vec4<f32>,
    @location(0) normal: vec3<f32>,
    @location(1) uv : vec2<f32>,
    @location(2) color : vec3<f32>,
    @location(3) viewPosition: vec3<f32>
}

// Skinning Calculation
fn calcSkinnedPosition(position: vec3<f32>, boneWeights: vec4<f32>, boneIndices: vec4<u32>) -> vec4<f32> {
    var skinnedPosition = vec4<f32>(0.0);
    var totalWeight = 0.0;

    for (var i = 0u; i < 4u; i = i + 1u) {
        if (boneWeights[i] > 0.0) {
            let boneID = boneIndices[i];
            let skinMatrix = skinUniforms.bones[boneID] * skinUniforms.invBindMatrix[boneID];
            skinnedPosition = skinnedPosition + skinMatrix * vec4<f32>(position, 1.0) * boneWeights[i];
            totalWeight = totalWeight + boneWeights[i];
        }
    }

    if (totalWeight > 0.0) {
        return skinnedPosition / totalWeight;
    } else {
        return vec4<f32>(position, 1.0);
    }
}

// Vertex Shader
@vertex
fn vs_main(input: VertexInput) -> VertexOutput {
    var output: VertexOutput;

    let skinnedPosition = calcSkinnedPosition(input.position, input.boneWeights, input.boneIndices);
    output.position = sceneUniforms.modelViewProjectionMatrix * skinnedPosition;

    // Transform normal using the view matrix (simplification for now)
    output.normal = normalize((sceneUniforms.viewMatrix * vec4<f32>(input.normal, 0.0)).xyz);
    output.uv = input.uv;
    output.color = vec3<f32>(1.0); // Pass white as default vertex color
    output.viewPosition = (sceneUniforms.viewMatrix * skinnedPosition).xyz;

    return output;
}

// PBR Functions
fn DistributionGGX(N: vec3<f32>, H: vec3<f32>, roughness: f32) -> f32 {
    let a = roughness * roughness;
    let a2 = a * a;
    let NdotH = max(dot(N, H), 0.0);
    let NdotH2 = NdotH * NdotH;

    let nom = a2;
    var denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;
    return nom / denom;
}

fn GeometrySchlickGGX(NdotV: f32, roughness: f32) -> f32 {
    let r = roughness + 1.0;
    let k = (r * r) / 8.0;
    let nom = NdotV;
    let denom = NdotV * (1.0 - k) + k;
    return nom / denom;
}

fn GeometrySmith(N: vec3<f32>, V: vec3<f32>, L: vec3<f32>, roughness: f32) -> f32 {
    let NdotV = max(dot(N, V), 0.0);
    let NdotL = max(dot(N, L), 0.0);
    let ggx2 = GeometrySchlickGGX(NdotV, roughness);
    let ggx1 = GeometrySchlickGGX(NdotL, roughness);
    return ggx1 * ggx2;
}

fn fresnelSchlick(cosTheta: f32, F0: vec3<f32>) -> vec3<f32> {
    return F0 + (vec3<f32>(1.0) - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

fn calculatePBRColor(baseColor: vec4<f32>, normal: vec3<f32>, viewPosition: vec3<f32>) -> vec3<f32> {
    let lightPos = vec3<f32>(5.0, 5.0, 5.0);
    let lightColor = vec3<f32>(1.0, 1.0, 1.0) * 2.0;

    let albedo = pbrMaterial.color.rgb * baseColor.rgb;
    let metalness = pbrMaterial.metalness;
    let roughness = pbrMaterial.roughness;

    let N = normalize(normal);
    let V = normalize(-viewPosition);
    let L = normalize(lightPos);
    let H = normalize(V + L);

    var F0 = vec3<f32>(0.04);
    F0 = mix(F0, albedo, metalness);

    let NDF = DistributionGGX(N, H, roughness);
    let G = GeometrySmith(N, V, L, roughness);
    let F = fresnelSchlick(max(dot(H, V), 0.0), F0);

    let kS = F;
    var kD = vec3<f32>(1.0) - kS;
    kD = kD * (1.0 - metalness);

    let numerator = NDF * G * F;
    let denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
    let specular = numerator / denominator;

    let NdotL = max(dot(N, L), 0.0);
    let Lo = (kD * albedo / PI + specular) * lightColor * NdotL;

    let ambient = vec3<f32>(0.03) * albedo;
    return ambient + Lo;
}

// Fragment Shader
@fragment
fn fs_main(input: VertexOutput) -> @location(0) vec4<f32> {
    var baseColor = textureSample(t_diffuse, s_diffuse, input.uv);
    // Fallback to vertex color if texture is not sufficient
    if (baseColor.a < 0.1) {
        baseColor = vec4<f32>(input.color, 1.0);
    }

    let pbrColor = calculatePBRColor(baseColor, input.normal, input.viewPosition);
    let emissive = pbrMaterial.emissive.rgb * pbrMaterial.emissiveIntensity;
    var finalColor = pbrColor + emissive;

    // Tonemapping and gamma correction
    finalColor = finalColor / (finalColor + vec3<f32>(1.0));
    finalColor = pow(finalColor, vec3<f32>(1.0/2.2));

    return vec4<f32>(finalColor, baseColor.a);
}
"""
