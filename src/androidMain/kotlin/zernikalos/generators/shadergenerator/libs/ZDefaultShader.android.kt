/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator.libs

const val defaultVertexShaderSource = """

uniform u_sceneMatrixBlock
{
  mat4 projMatrix;
  mat4 viewMatrix;
  mat4 mvpMatrix;
} u_sceneMatrix;

#ifdef USE_SKINNING
    uniform u_skinningMatrixBlock
    {
        mat4 bones[100];
        mat4 invBindMatrix[100];
    } skinUniforms;
#endif

#ifdef USE_PBR_MATERIAL
uniform u_pbrMaterialBlock
{
    vec3 color;
    vec3 emissive;
    float emissiveIntensity;
    float metalness;
    float roughness;
} u_pbrMaterial;
#endif

#ifdef USE_SKINNING
    in vec4 a_boneIndices;
    in vec4 a_boneWeight;
#endif
#ifdef USE_NORMALS
    in vec3 a_normal;
    out vec3 v_normal;
#endif
#ifdef USE_TEXTURES
    in vec2 a_uv;
    out vec2 v_uv;
#endif
#ifdef USE_COLORS
    in vec3 a_color;
    out vec3 v_color;
#endif
in vec3 a_position;

#ifdef USE_SKINNING
vec4 calcSkinnedPosition() {
    vec4 skinnedPosition = vec4(0.0);
    float totalWeight = 0.0;

     // Sum the transformations from each influencing bone
    for (int i = 0; i < 4; ++i) {
        if (a_boneWeight[i] > 0.0) {
            int boneID = int(a_boneIndices[i]);
            mat4 skinMatrix = skinUniforms.bones[boneID] * skinUniforms.invBindMatrix[boneID];
            vec4 posedPosition = skinMatrix * vec4(a_position, 1.0);

            skinnedPosition += a_boneWeight[i] * posedPosition;
            totalWeight += a_boneWeight[i];
        }
    }

    return totalWeight > 0.0 ? skinnedPosition / totalWeight : vec4(a_position, 1.0);
}
#endif

void main() {
    #ifdef USE_SKINNING
        gl_Position = u_sceneMatrix.mvpMatrix * calcSkinnedPosition();
    #else
        gl_Position = u_sceneMatrix.mvpMatrix * vec4(a_position,1);
    #endif

#ifdef USE_NORMALS
    v_normal = mat3(transpose(inverse(u_sceneMatrix.viewMatrix))) * a_normal;
#endif
#ifdef USE_TEXTURES
    #ifdef FLIP_TEXTURE_Y
        v_uv = vec2(a_uv.x, 1.0 - a_uv.y);;
    #else
        v_uv = a_uv;
    #endif
#endif
#ifdef USE_COLORS
    v_color = a_color;
#endif
}
"""

const val defaultFragmentShaderSource = """
precision mediump float;

#ifdef USE_NORMALS
    in vec3 v_normal;
#endif

uniform u_sceneMatrixBlock
{
    mat4 projMatrix;
    mat4 viewMatrix;
    mat4 mvpMatrix;
} u_sceneMatrix;

#ifdef USE_PBR_MATERIAL
uniform u_pbrMaterialBlock
{
    vec3 color;
    vec3 emissive;
    float emissiveIntensity;
    float metalness;
    float roughness;
} u_pbrMaterial;
#endif

#ifdef USE_TEXTURES
    uniform sampler2D u_texture;
    smooth in vec2 v_uv;
#endif
#ifdef USE_COLORS
    smooth in vec3 v_color;
#endif
out vec4 f_color;

// Basic PBR calculation constants
const float PI = 3.14159265359;

// Calculates the distribution of microfacets using the Trowbridge-Reitz GGX formula.
float DistributionGGX(vec3 N, vec3 H, float roughness) {
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
float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = GeometrySchlickGGX(NdotV, roughness);
    float ggx1 = GeometrySchlickGGX(NdotL, roughness);
    return ggx1 * ggx2;
}

// Calculates the Fresnel effect, which describes the ratio of reflected light.
vec3 fresnelSchlick(float cosTheta, vec3 F0) {
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

vec3 calculatePBRColor(vec4 baseColor, vec3 normal) {
    // Basic lighting properties (hardcoded for now)
    vec3 lightPos = vec3(5.0, 5.0, 5.0);
    vec3 lightColor = vec3(1.0, 1.0, 1.0) * 2.0; // Light intensity

    // Material properties from uniform
    vec3 albedo = u_pbrMaterial.color * baseColor.rgb;
    float metalness = u_pbrMaterial.metalness;
    float roughness = u_pbrMaterial.roughness;

    // Vector calculations
    vec3 N = normalize(normal);
    vec3 V = normalize(inverse(mat3(u_sceneMatrix.viewMatrix))[2]); // View direction
    vec3 L = normalize(lightPos); // For a directional light
    vec3 H = normalize(V + L);

    // Fresnel at normal incidence (F0)
    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo, metalness);

    // Cook-Torrance BRDF
    float NDF = DistributionGGX(N, H, roughness);
    float G = GeometrySmith(N, V, L, roughness);
    vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

    vec3 kS = F;
    vec3 kD = vec3(1.0) - kS;
    kD *= 1.0 - metalness;

    vec3 numerator = NDF * G * F;
    float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001; // Add epsilon to avoid division by zero
    vec3 specular = numerator / denominator;

    // Add light contribution
    float NdotL = max(dot(N, L), 0.0);
    vec3 Lo = (kD * albedo / PI + specular) * lightColor * NdotL;

    // Ambient light (a simple approximation)
    vec3 ambient = vec3(0.03) * albedo;
    vec3 color = ambient + Lo;

    // HDR Tonemapping
    color = color / (color + vec3(1.0));
    return pow(color, vec3(1.0/2.2));
}

void main() {
    vec4 baseColor = vec4(1.0);

    #if defined(USE_TEXTURES)
        baseColor = texture(u_texture, v_uv);
    #elif defined(USE_COLORS)
        baseColor = vec4(v_color.xyz, 1.0);
    #endif

    #if defined(USE_PBR_MATERIAL) && defined(USE_NORMALS)
        vec3 pbrColor = calculatePBRColor(baseColor, v_normal);
        f_color = vec4(pbrColor, baseColor.a);
    #else
        f_color = baseColor;
    #endif
}
"""
