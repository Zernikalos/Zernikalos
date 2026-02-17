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

val phongSkinningShaderSource = """
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

struct PhongMaterialUniforms {
    ambient: vec4<f32>,
    diffuse: vec4<f32>,
    specular: vec4<f32>,
    shininess: f32
}
@binding(${UNIFORM_IDS.BLOCK_PHONG_MATERIAL}) @group(0) var<uniform> phongMaterial: PhongMaterialUniforms;

// Bindings
@group(1) @binding(0) var t_diffuse: texture_2d<f32>;
@group(1) @binding(1) var s_diffuse: sampler;

// Vertex Data
struct VertexInput {
    @location(${ZAttributeId.POSITION.id}) position: vec3<f32>,
    @location(${ZAttributeId.NORMAL.id}) normal: vec3<f32>,
    @location(${ZAttributeId.UV.id}) uv : vec2<f32>,
    @location(${ZAttributeId.BONE_WEIGHT.id}) boneWeights : vec4<f32>,
    @location(${ZAttributeId.BONE_INDEX.id}) boneIndices : vec4<u32>,
}

struct VertexOutput {
    @builtin(position) position : vec4<f32>,
    @location(0) normal: vec3<f32>,
    @location(1) uv : vec2<f32>,
    @location(2) viewPosition: vec3<f32>
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

    // Transform normal using the view matrix
    output.normal = normalize((sceneUniforms.viewMatrix * vec4<f32>(input.normal, 0.0)).xyz);
    output.uv = input.uv;
    output.viewPosition = (sceneUniforms.viewMatrix * skinnedPosition).xyz;

    return output;
}

// Blinn-Phong Lighting Functions
fn calculateBlinnPhongColor(baseColor: vec4<f32>, normal: vec3<f32>, viewPosition: vec3<f32>) -> vec3<f32> {
    let lightPos = vec3<f32>(5.0, -5.0, 5.0);
    let lightColor = vec3<f32>(1.0, 1.0, 1.0) * 2.0;

    let ambient = phongMaterial.ambient.rgb;
    let diffuse = phongMaterial.diffuse.rgb * baseColor.rgb;
    let specular = phongMaterial.specular.rgb;
    let shininess = phongMaterial.shininess;

    let N = normalize(normal);
    let V = normalize(-viewPosition);
    let L = normalize(lightPos);
    let H = normalize(V + L);

    // Ambient component
    let ambientComponent = ambient * baseColor.rgb;

    // Diffuse component
    let NdotL = max(dot(N, L), 0.0);
    let diffuseComponent = diffuse * lightColor * NdotL;

    // Specular component (Blinn-Phong)
    let NdotH = max(dot(N, H), 0.0);
    let specularComponent = specular * lightColor * pow(NdotH, shininess);

    // Combine all components
    let finalColor = ambientComponent + diffuseComponent + specularComponent;
    
    return finalColor;
}

// Fragment Shader
@fragment
fn fs_main(input: VertexOutput) -> @location(0) vec4<f32> {
    var baseColor = textureSample(t_diffuse, s_diffuse, input.uv);
    
    // Fallback to white if texture alpha is too low
    if (baseColor.a < 0.1) {
        baseColor = vec4<f32>(1.0);
    }

    let phongColor = calculateBlinnPhongColor(baseColor, input.normal, input.viewPosition);
    
    // Gamma correction
    let finalColor = pow(phongColor, vec3<f32>(1.0/2.2));

    return vec4<f32>(finalColor, baseColor.a);
}
"""
