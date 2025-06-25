/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator.libs

const val defaultShaderSource = """
struct Uniforms {
    projectionMatrix : mat4x4<f32>,
    viewMatrix : mat4x4<f32>,
    modelViewProjectionMatrix : mat4x4<f32>
}
@binding(10) @group(0) var<uniform> uniforms : Uniforms;

@group(1) @binding(0) var t_diffuse: texture_2d<f32>;
@group(1) @binding(1) var s_diffuse: sampler;

struct VertexInput {
    @location(1) position: vec3<f32>,
    @location(4) uv : vec2<f32>,
    @location(5) boneWeights : vec4<f32>,
    @location(6) boneIndices : vec4<u32>,
}

struct VertexOutput {
    @builtin(position) Position : vec4<f32>,
    @location(0) v_uv : vec2<f32>
}

@vertex
fn vs_main(input: VertexInput) -> VertexOutput {
    var output : VertexOutput;
    output.Position = uniforms.modelViewProjectionMatrix * vec4<f32>(input.position, 1.0);
    output.v_uv = input.uv;
    return output;
}

@fragment
fn fs_main(in: VertexOutput) -> @location(0) vec4<f32> {
    return textureSample(t_diffuse, s_diffuse, in.v_uv);
}
"""
