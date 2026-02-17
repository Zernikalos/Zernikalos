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

val skinningNoPbrShaderSource = """
struct Uniforms {
    projectionMatrix : mat4x4<f32>,
    viewMatrix : mat4x4<f32>,
    modelViewProjectionMatrix : mat4x4<f32>
}
@binding(${UNIFORM_IDS.BLOCK_SCENE_MATRIX}) @group(0) var<uniform> uniforms : Uniforms;

struct SkinningUniforms {
    bones : array<mat4x4<f32>, 100>,
    invBindMatrix : array<mat4x4<f32>, 100>
}
@binding(${UNIFORM_IDS.BLOCK_SKINNING_MATRIX}) @group(0) var<uniform> skinUniforms : SkinningUniforms;

@group(1) @binding(0) var t_diffuse: texture_2d<f32>;
@group(1) @binding(1) var s_diffuse: sampler;

struct VertexInput {
    @location(${ZAttributeId.POSITION.id}) position: vec3<f32>,
    @location(${ZAttributeId.UV.id}) uv : vec2<f32>,
    @location(${ZAttributeId.BONE_WEIGHT.id}) boneWeights : vec4<f32>,
    @location(${ZAttributeId.BONE_INDEX.id}) boneIndices : vec4<u32>,
}

struct VertexOutput {
    @builtin(position) Position : vec4<f32>,
    @location(0) v_uv : vec2<f32>
}

fn calcSkinnedPosition(position: vec3<f32>, boneWeights: vec4<f32>, boneIndices: vec4<u32>) -> vec4<f32> {
    var skinnedPosition = vec4<f32>(0.0, 0.0, 0.0, 0.0);
    var totalWeight = 0.0;

    for (var i = 0u; i < 4u; i = i + 1u) {
        if (boneWeights[i] > 0.0) {
            let boneID = boneIndices[i];
            let skinMatrix = skinUniforms.bones[boneID] * skinUniforms.invBindMatrix[boneID];
            let posedPosition = skinMatrix * vec4<f32>(position, 1.0);

            skinnedPosition = skinnedPosition + boneWeights[i] * posedPosition;
            totalWeight = totalWeight + boneWeights[i];
        }
    }

    if (totalWeight > 0.0) {
        return skinnedPosition / totalWeight;
    } else {
        return vec4<f32>(position, 1.0);
    }
}

@vertex
fn vs_main(input: VertexInput) -> VertexOutput {
    var output : VertexOutput;
    let skinned_position = calcSkinnedPosition(input.position, input.boneWeights, input.boneIndices);
    output.Position = uniforms.modelViewProjectionMatrix * skinned_position;
    output.v_uv = input.uv;
    return output;
}

@fragment
fn fs_main(in: VertexOutput) -> @location(0) vec4<f32> {
    return textureSample(t_diffuse, s_diffuse, in.v_uv);
}
"""
