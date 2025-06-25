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

struct VertexInput {
    @location(1) position: vec3<f32>,
    @location(4) uv : vec2<f32>,
    @location(5) boneWeights : vec4<f32>,
    @location(6) boneIndices : vec4<u32>,
}

struct VertexOutput {
    @builtin(position) Position : vec4<f32>,
    @location(0) vColor : vec3<f32>
}

@vertex
fn vs_main(input: VertexInput) -> VertexOutput {
    var output : VertexOutput;
    output.Position = uniforms.modelViewProjectionMatrix * vec4<f32>(input.position, 1.0);
    output.vColor = input.position * 0.5 + vec3<f32>(0.5, 0.5, 0.5); // Color basado en posición
    return output;
}

@fragment
fn fs_main(@location(0) vColor : vec3<f32>) -> @location(0) vec4<f32> {
    return vec4<f32>(vColor, 1.0);
}
"""
