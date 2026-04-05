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

//#ifdef USE_SKINNING
struct SkinningUniforms {
    bones : array<mat4x4<f32>, 100>,
    invBindMatrix : array<mat4x4<f32>, 100>
}
@binding(${UNIFORM_IDS.BLOCK_SKINNING_MATRIX}) @group(0) var<uniform> skinUniforms : SkinningUniforms;

struct ModelSkinningUniforms {
    modelSkinBindMatrix : mat4x4<f32>,
    modelSkinBindMatrixInverse : mat4x4<f32>
}
@binding(${UNIFORM_IDS.BLOCK_MODEL_SKINNING_MATRIX}) @group(0) var<uniform> modelSkinning : ModelSkinningUniforms;
//#endif

//#ifdef USE_PBR_MATERIAL
struct PBRMaterialUniforms {
    color: vec4<f32>,
    emissive: vec4<f32>,
    emissiveIntensity: f32,
    metalness: f32,
    roughness: f32
}
@binding(${UNIFORM_IDS.BLOCK_PBR_MATERIAL}) @group(0) var<uniform> pbrMaterial: PBRMaterialUniforms;
//#endif

//#ifdef USE_LIGHTING
// MAX_DIRECT_LIGHTS must match Kotlin MAX_DIRECT_LIGHTS.
const MAX_DIRECT_LIGHTS: u32 = 4u;
struct DirectLight {
    direction: vec4<f32>,
    position: vec4<f32>,
    color: vec4<f32>,
    intensity: f32,
    lampType: f32,
    range: f32,
    decay: f32,
    innerAngle: f32,
    outerAngle: f32,
}
struct LightUniforms {
    lights: array<DirectLight, 4>,
    directCount: f32,
}
@binding(${UNIFORM_IDS.BLOCK_LIGHT}) @group(0) var<uniform> light: LightUniforms;

struct AmbientLightUniforms {
    ambientColor: vec4<f32>,
    ambientIntensity: f32,
}
@binding(${UNIFORM_IDS.BLOCK_AMBIENT_LIGHT}) @group(0) var<uniform> ambientLight: AmbientLightUniforms;
//#endif

//#ifdef USE_TEXTURES
@group(1) @binding(0) var t_diffuse: texture_2d<f32>;
@group(1) @binding(1) var s_diffuse: sampler;
//#endif

// Vertex Data
struct VertexInput {
    @location(${ZAttributeId.POSITION.id}) position: vec3<f32>,
//#ifdef USE_NORMALS
    @location(${ZAttributeId.NORMAL.id}) normal: vec3<f32>,
//#endif
//#ifdef USE_TEXTURES
    @location(${ZAttributeId.UV.id}) uv : vec2<f32>,
//#endif
//#ifdef USE_COLORS
    @location(${ZAttributeId.COLOR.id}) color : vec3<f32>,
//#endif
//#ifdef USE_SKINNING
    @location(${ZAttributeId.BONE_WEIGHT.id}) boneWeights : vec4<f32>,
    @location(${ZAttributeId.BONE_INDEX.id}) boneIndices : vec4<u32>,
//#endif
}

struct VertexOutput {
    @builtin(position) position : vec4<f32>,
//#ifdef USE_NORMALS
    @location(0) normal: vec3<f32>,
//#endif
//#ifdef USE_TEXTURES
    @location(1) uv : vec2<f32>,
//#endif
//#ifdef USE_COLORS
    @location(2) color : vec3<f32>,
//#endif
//#ifdef USE_PBR_MATERIAL
    @location(3) viewPosition: vec3<f32>,
//#else
    //#ifdef USE_LIGHTING
    @location(3) viewPosition: vec3<f32>,
    //#endif
//#endif
}

//#ifdef USE_SKINNING
/*
 * Computes the skinned position in mesh local space.
 *
 * Pipeline:
 * 1. Transform vertex to skeleton space: modelSkinBindMatrix * position
 *    (when mesh and skeleton share the same node, modelSkinBindMatrix is identity)
 * 2. Apply weighted blend of per-bone skin matrices (bones[i] * invBindMatrix[i])
 * 3. Transform back to mesh space: modelSkinBindMatrixInverse * blended
 *
 * Formula: modelSkinBindMatrixInverse * blended, where blended is the weighted sum of
 * (bones[i] * invBindMatrix[i] * posInSkeletonSpace) and posInSkeletonSpace = modelSkinBindMatrix * position.
 */
fn calcSkinnedPosition(position: vec3<f32>, boneWeights: vec4<f32>, boneIndices: vec4<u32>) -> vec4<f32> {
    let posInSkeletonSpace = modelSkinning.modelSkinBindMatrix * vec4<f32>(position, 1.0);
    var skinnedPosition = vec4<f32>(0.0);
    var totalWeight = 0.0;

    for (var i = 0u; i < 4u; i = i + 1u) {
        if (boneWeights[i] > 0.0) {
            let boneID = boneIndices[i];
            let skinMatrix = skinUniforms.bones[boneID] * skinUniforms.invBindMatrix[boneID];
            skinnedPosition = skinnedPosition + skinMatrix * posInSkeletonSpace * boneWeights[i];
            totalWeight = totalWeight + boneWeights[i];
        }
    }

    var blended = posInSkeletonSpace;
    if (totalWeight > 0.0) {
        blended = skinnedPosition / totalWeight;
    }
    return modelSkinning.modelSkinBindMatrixInverse * blended;
}
//#endif

// Vertex Shader
@vertex
fn vs_main(input: VertexInput) -> VertexOutput {
    var output: VertexOutput;

//#ifdef USE_SKINNING
    let finalPosition = calcSkinnedPosition(input.position, input.boneWeights, input.boneIndices);
//#else
    let finalPosition = vec4<f32>(input.position, 1.0);
//#endif

    output.position = sceneUniforms.modelViewProjectionMatrix * finalPosition;

//#ifdef USE_NORMALS
    output.normal = normalize((sceneUniforms.viewMatrix * vec4<f32>(input.normal, 0.0)).xyz);
//#endif

//#ifdef USE_TEXTURES
    //#ifdef FLIP_TEXTURE_Y
    output.uv = vec2<f32>(input.uv.x, 1.0 - input.uv.y);
    //#else
    output.uv = input.uv;
    //#endif
//#endif

//#ifdef USE_COLORS
    output.color = input.color;
//#else
    //#ifdef USE_PBR_MATERIAL
    // No vertex colors, will use material color in fragment shader
    //#endif
//#endif

//#ifdef USE_PBR_MATERIAL
    output.viewPosition = (sceneUniforms.viewMatrix * finalPosition).xyz;
//#else
    //#ifdef USE_LIGHTING
    output.viewPosition = (sceneUniforms.viewMatrix * finalPosition).xyz;
    //#endif
//#endif

    return output;
}

//#ifdef USE_LIGHTING
// Computes light direction and attenuation for any light type (per entry in light.lights[idx]).
// Returns: vec4 where xyz = light direction, w = attenuation
// lampType: 0 = directional, 1 = point, 2 = spot
fn computeLightContribution(idx: u32, fragPosition: vec3<f32>) -> vec4<f32> {
    var lightDir: vec3<f32>;
    var attenuation: f32 = 1.0;
    let L = light.lights[idx];

    if (L.lampType < 0.5) {
        // Directional light
        lightDir = normalize(-L.direction.xyz);
    } else {
        // Point / Spot light
        let toLight = L.position.xyz - fragPosition;
        let dist = length(toLight);
        lightDir = normalize(toLight);

        let maxRange = max(L.range, 0.0001);
        attenuation = pow(clamp(1.0 - dist / maxRange, 0.0, 1.0), max(L.decay, 1.0));

        if (L.lampType > 1.5) {
            // Spot light cone attenuation
            let cosAngle = dot(-lightDir, normalize(L.direction.xyz));
            let cosInner = cos(L.innerAngle);
            let cosOuter = cos(L.outerAngle);
            let spotFactor = clamp((cosAngle - cosOuter) / (cosInner - cosOuter + 0.0001), 0.0, 1.0);
            attenuation = attenuation * spotFactor;
        }
    }

    return vec4<f32>(lightDir, attenuation);
}
//#endif

//#ifdef USE_PBR_MATERIAL
//#ifdef USE_LIGHTING
// Indirect / ambient term from global AmbientLight (separate from direct lights[]).
fn calculatePBRAmbient(baseColor: vec4<f32>) -> vec3<f32> {
    // Material properties from uniform
    let albedo = pbrMaterial.color.rgb * baseColor.rgb;
    let metalness = pbrMaterial.metalness;
    var F0 = vec3<f32>(0.04);
    F0 = mix(F0, albedo, metalness);
    let kD = (vec3<f32>(1.0) - F0) * (1.0 - metalness);
    return ambientLight.ambientColor.rgb * ambientLight.ambientIntensity * albedo * kD;
}
//#endif

// Calculates the distribution of microfacets using the Trowbridge-Reitz GGX formula.
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

// Calculates the geometric obstruction of microfacets.
fn GeometrySchlickGGX(NdotV: f32, roughness: f32) -> f32 {
    let r = roughness + 1.0;
    let k = (r * r) / 8.0;
    let nom = NdotV;
    let denom = NdotV * (1.0 - k) + k;
    return nom / denom;
}

// Calculates the geometry factor for both direct and view vectors.
fn GeometrySmith(N: vec3<f32>, V: vec3<f32>, L: vec3<f32>, roughness: f32) -> f32 {
    let NdotV = max(dot(N, V), 0.0);
    let NdotL = max(dot(N, L), 0.0);
    let ggx2 = GeometrySchlickGGX(NdotV, roughness);
    let ggx1 = GeometrySchlickGGX(NdotL, roughness);
    return ggx1 * ggx2;
}

// Calculates the Fresnel effect, which describes the ratio of reflected light.
fn fresnelSchlick(cosTheta: f32, F0: vec3<f32>) -> vec3<f32> {
    return F0 + (vec3<f32>(1.0) - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

fn calculatePBRLoDirect(baseColor: vec4<f32>, normal: vec3<f32>, viewPosition: vec3<f32>, lightDir: vec3<f32>, lightColor: vec3<f32>) -> vec3<f32> {
    // Material properties from uniform
    let albedo = pbrMaterial.color.rgb * baseColor.rgb;
    let metalness = pbrMaterial.metalness;
    let roughness = pbrMaterial.roughness;

    // Vector calculations
    let N = normalize(normal);
    let V = normalize(-viewPosition);
    let L = lightDir;
    let H = normalize(V + L);

    // Fresnel at normal incidence (F0)
    var F0 = vec3<f32>(0.04);
    F0 = mix(F0, albedo, metalness);

    // Cook-Torrance BRDF
    let NDF = DistributionGGX(N, H, roughness);
    let G = GeometrySmith(N, V, L, roughness);
    let F = fresnelSchlick(max(dot(H, V), 0.0), F0);

    let kS = F;
    var kD = vec3<f32>(1.0) - kS;
    kD = kD * (1.0 - metalness);

    let numerator = NDF * G * F;
    let denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
    let specular = numerator / denominator;

    // Add light contribution
    let NdotL = max(dot(N, L), 0.0);
    return (kD * albedo / PI + specular) * lightColor * NdotL;
}

fn calculatePBRColorNoLighting(baseColor: vec4<f32>, normal: vec3<f32>, viewPosition: vec3<f32>, lightDir: vec3<f32>, lightColor: vec3<f32>) -> vec3<f32> {
    // Material properties from uniform
    let albedo = pbrMaterial.color.rgb * baseColor.rgb;

    // Ambient light (fallback when USE_LIGHTING is off)
    let ambient = vec3<f32>(0.03) * albedo;
    return ambient + calculatePBRLoDirect(baseColor, normal, viewPosition, lightDir, lightColor);
}
//#endif

// Tonemapping and gamma correction
fn applyTonemapping(color: vec3<f32>) -> vec3<f32> {
    var result = color / (color + vec3<f32>(1.0));
    return pow(result, vec3<f32>(1.0/2.2));
}

// Fragment Shader
@fragment
fn fs_main(input: VertexOutput) -> @location(0) vec4<f32> {
    var baseColor = vec4<f32>(1.0);

//#ifdef USE_TEXTURES
    baseColor = textureSample(t_diffuse, s_diffuse, input.uv);
//#endif

//#ifdef USE_COLORS
    //#ifndef USE_TEXTURES
    baseColor = vec4<f32>(input.color, 1.0);
    //#endif
    // If we have textures and colors, we could blend them here if needed
//#endif

//#ifdef USE_PBR_MATERIAL
    //#ifdef USE_NORMALS
        //#ifdef USE_LIGHTING
    // Direct lights: ambient term once + sum of per-light Cook-Torrance contributions.
    let n = i32(light.directCount + 0.5);
    var directSum = vec3<f32>(0.0);
    for (var i = 0; i < 4; i = i + 1) {
        if (i >= n) {
            break;
        }
        let lightResult = computeLightContribution(u32(i), input.viewPosition);
        let lDir = lightResult.xyz;
        let lAtten = lightResult.w;
        let lColor = light.lights[u32(i)].color.rgb * light.lights[u32(i)].intensity * lAtten;
        directSum = directSum + calculatePBRLoDirect(baseColor, input.normal, input.viewPosition, lDir, lColor);
    }
    let pbrColor = calculatePBRAmbient(baseColor) + directSum;
        //#else
    let pbrColor = calculatePBRColorNoLighting(baseColor, input.normal, input.viewPosition, normalize(vec3<f32>(5.0, 5.0, 5.0)), vec3<f32>(2.0));
        //#endif
    //#else
    let pbrColor = baseColor.rgb * pbrMaterial.color.rgb;
    //#endif

    // WORKAROUND: Emissive disabled until emissive maps are supported (see GitHub issue)
    // Without emissive maps, models with emissive=[1,1,1] wash out to white
    let emissive = pbrMaterial.emissive.rgb * min(pbrMaterial.emissiveIntensity, 0.0);
    let finalColor = applyTonemapping(pbrColor + emissive);
    return vec4<f32>(finalColor, baseColor.a);
//#else
    return baseColor;
//#endif
}
"""
