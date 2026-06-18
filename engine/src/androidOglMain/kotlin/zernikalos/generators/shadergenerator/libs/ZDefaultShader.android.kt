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

    uniform u_modelSkinningMatrixBlock
    {
        mat4 modelSkinBindMatrix;
        mat4 modelSkinBindMatrixInverse;
    } u_modelSkinningMatrix;
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
#ifdef USE_LIGHTING
    out vec3 v_viewPosition;
#endif
in vec3 a_position;

#ifdef USE_SKINNING
/*
 * Computes the skinned position in mesh local space.
 *
 * Pipeline:
 * 1. Transform vertex to skeleton space: modelSkinBindMatrix * position
 *    (when mesh and skeleton share the same node, modelSkinBindMatrix is identity)
 * 2. Apply weighted blend of per-bone skin matrices (bones[i] * invBindMatrix[i])
 * 3. Transform back to mesh space: modelSkinBindMatrixInverse * blended
 *
 * Formula: mvpMatrix * (modelSkinBindMatrixInverse * sum(weight * bones[i] * invBindMatrix[i] * modelSkinBindMatrix * position))
 */
vec4 calcSkinnedPosition() {
    vec4 posInSkeletonSpace = u_modelSkinningMatrix.modelSkinBindMatrix * vec4(a_position, 1.0);
    vec4 skinnedPosition = vec4(0.0);
    float totalWeight = 0.0;

    for (int i = 0; i < 4; ++i) {
        if (a_boneWeight[i] > 0.0) {
            int boneID = int(a_boneIndices[i]);
            mat4 skinMatrix = skinUniforms.bones[boneID] * skinUniforms.invBindMatrix[boneID];
            skinnedPosition += a_boneWeight[i] * skinMatrix * posInSkeletonSpace;
            totalWeight += a_boneWeight[i];
        }
    }

    vec4 blended = totalWeight > 0.0 ? skinnedPosition / totalWeight : posInSkeletonSpace;
    return u_modelSkinningMatrix.modelSkinBindMatrixInverse * blended;
}
#endif

void main() {
    vec4 finalPosition;
    #ifdef USE_SKINNING
        finalPosition = calcSkinnedPosition();
    #else
        finalPosition = vec4(a_position, 1.0);
    #endif

    gl_Position = u_sceneMatrix.mvpMatrix * finalPosition;

#ifdef USE_NORMALS
    v_normal = mat3(transpose(inverse(u_sceneMatrix.viewMatrix))) * a_normal;
#endif
#ifdef USE_LIGHTING
    v_viewPosition = (u_sceneMatrix.viewMatrix * finalPosition).xyz;
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
    vec4 color;
    vec4 emissive;
    float emissiveIntensity;
    float metalness;
    float roughness;
} u_pbrMaterial;
#endif

#ifdef USE_PHONG_MATERIAL
uniform u_phongMaterialBlock
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float shininess;
} u_phongMaterial;
#endif

#ifdef USE_LIGHTING
// MAX_DIRECT_LIGHTS must match zernikalos.generators.uniformgenerator.MAX_DIRECT_LIGHTS (Kotlin).
const int MAX_DIRECT_LIGHTS = 4;
struct DirectLight {
    vec4 direction;
    vec4 position;
    vec4 color;
    float intensity;
    float lampType;
    float range;
    float decay;
    float innerAngle;
    float outerAngle;
};
uniform u_lightBlock
{
    DirectLight lights[MAX_DIRECT_LIGHTS];
    float directCount;
} u_light;

uniform u_ambientLightBlock
{
    vec4 ambientColor;
    float ambientIntensity;
} u_ambientLight;
in vec3 v_viewPosition;
#endif

#ifdef USE_TEXTURES
    uniform sampler2D u_texture;
    smooth in vec2 v_uv;
#endif
#ifdef USE_COLORS
    smooth in vec3 v_color;
#endif
out vec4 f_color;

const float PI = 3.14159265359;

// Tonemapping (Reinhard) and gamma correction
vec3 applyTonemapping(vec3 color) {
    color = color / (color + vec3(1.0));
    return pow(color, vec3(1.0/2.2));
}

#ifdef USE_LIGHTING
// Computes light direction and attenuation for any light type (per entry in u_light.lights[idx]).
void computeLightContribution(int idx, vec3 fragPosition, out vec3 outLightDir, out float outAttenuation) {
    DirectLight L = u_light.lights[idx];
    outAttenuation = 1.0;

    if (L.lampType < 0.5) {
        // Directional light
        outLightDir = normalize(-L.direction.xyz);
    } else {
        // Point / Spot light
        vec3 toLight = L.position.xyz - fragPosition;
        float dist = length(toLight);
        outLightDir = normalize(toLight);

        float maxRange = max(L.range, 0.0001);
        outAttenuation = pow(clamp(1.0 - dist / maxRange, 0.0, 1.0), max(L.decay, 1.0));

        if (L.lampType > 1.5) {
            // Spot light cone attenuation
            float cosAngle = dot(-outLightDir, normalize(L.direction.xyz));
            float cosInner = cos(L.innerAngle);
            float cosOuter = cos(L.outerAngle);
            float spotFactor = clamp((cosAngle - cosOuter) / (cosInner - cosOuter + 0.0001), 0.0, 1.0);
            outAttenuation *= spotFactor;
        }
    }
}
#endif

#ifdef USE_PBR_MATERIAL
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

#ifdef USE_LIGHTING
    // Indirect / ambient term from global AmbientLight (separate from direct lights[]).
    vec3 calculatePBRAmbient(vec4 baseColor) {
        // Material properties from uniform
        vec3 albedo = u_pbrMaterial.color.rgb * baseColor.rgb;
        float metalness = u_pbrMaterial.metalness;
        vec3 F0 = vec3(0.04);
        F0 = mix(F0, albedo, metalness);
        vec3 kD = (vec3(1.0) - F0) * (1.0 - metalness);
        return u_ambientLight.ambientColor.rgb * u_ambientLight.ambientIntensity * albedo * kD;
    }
#endif

    vec3 calculatePBRLoDirect(vec4 baseColor, vec3 normal, vec3 viewPos, vec3 lightDir, vec3 lightColor) {
        // Material properties from uniform
        vec3 albedo = u_pbrMaterial.color.rgb * baseColor.rgb;
        float metalness = u_pbrMaterial.metalness;
        float roughness = u_pbrMaterial.roughness;

        // Vector calculations
        vec3 N = normalize(normal);
        vec3 V = normalize(-viewPos);
        vec3 L = lightDir;
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
        float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
        vec3 specular = numerator / denominator;

        // Add light contribution
        float NdotL = max(dot(N, L), 0.0);
        vec3 Lo = (kD * albedo / PI + specular) * lightColor * NdotL;

        return Lo;
    }

    vec3 calculatePBRColorNoLighting(vec4 baseColor, vec3 normal, vec3 viewPos, vec3 lightDir, vec3 lightColor) {
        // Material properties from uniform
        vec3 albedo = u_pbrMaterial.color.rgb * baseColor.rgb;

        // Ambient light
        vec3 ambient = vec3(0.03) * albedo;
        vec3 color = ambient + calculatePBRLoDirect(baseColor, normal, viewPos, lightDir, lightColor);

        return color;
    }
#endif

#ifdef USE_PHONG_MATERIAL
#ifdef USE_LIGHTING
    // Global ambient from AmbientLight * material ambient * base color.
    vec3 calculatePhongAmbient(vec4 baseColor) {
        return u_ambientLight.ambientColor.rgb * u_ambientLight.ambientIntensity * u_phongMaterial.ambient.rgb * baseColor.rgb;
    }

    vec3 calculatePhongDirect(vec4 baseColor, vec3 normal, vec3 viewPos, vec3 lightDir, vec3 lightColor) {
        // Material properties from uniform
        vec3 diffuse = u_phongMaterial.diffuse.rgb * baseColor.rgb;
        vec3 specular = u_phongMaterial.specular.rgb;
        float shininess = u_phongMaterial.shininess;

        // Vector calculations
        vec3 N = normalize(normal);
        vec3 V = normalize(-viewPos);
        vec3 L = lightDir;
        vec3 H = normalize(V + L);

        // Diffuse component
        float NdotL = max(dot(N, L), 0.0);
        vec3 diffuseComponent = diffuse * lightColor * NdotL;

        // Specular component (Blinn-Phong)
        float NdotH = max(dot(N, H), 0.0);
        vec3 specularComponent = specular * lightColor * pow(NdotH, shininess);

        // Combine all components
        vec3 finalColor = diffuseComponent + specularComponent;

        return finalColor;
    }
#endif

    // Blinn-Phong lighting calculation
    vec3 calculateBlinnPhongColorNoLighting(vec4 baseColor, vec3 normal, vec3 viewPos, vec3 lightDir, vec3 lightColor) {
        // Material properties from uniform
        vec3 ambient = u_phongMaterial.ambient.rgb;
        vec3 diffuse = u_phongMaterial.diffuse.rgb * baseColor.rgb;
        vec3 specular = u_phongMaterial.specular.rgb;
        float shininess = u_phongMaterial.shininess;

        // Vector calculations
        vec3 N = normalize(normal);
        vec3 V = normalize(-viewPos);
        vec3 L = lightDir;
        vec3 H = normalize(V + L);

        // Ambient component
        vec3 ambientComponent = ambient * baseColor.rgb;

        // Diffuse component
        float NdotL = max(dot(N, L), 0.0);
        vec3 diffuseComponent = diffuse * lightColor * NdotL;

        // Specular component (Blinn-Phong)
        float NdotH = max(dot(N, H), 0.0);
        vec3 specularComponent = specular * lightColor * pow(NdotH, shininess);

        // Combine all components
        vec3 finalColor = ambientComponent + diffuseComponent + specularComponent;

        return finalColor;
    }
#endif

void main() {
    vec4 baseColor = vec4(1.0);

    #if defined(USE_TEXTURES)
        baseColor = texture(u_texture, v_uv);
    #elif defined(USE_COLORS)
        baseColor = vec4(v_color.xyz, 1.0);
    #endif

    #if defined(USE_PBR_MATERIAL)
        // WORKAROUND: Emissive disabled until emissive maps are supported (see GitHub issue)
        // Without emissive maps, models with emissive=[1,1,1] wash out to white
        vec3 emissive = u_pbrMaterial.emissive.rgb * min(u_pbrMaterial.emissiveIntensity, 0.0);

        #if defined(USE_NORMALS)
            #if defined(USE_LIGHTING)
                // Direct lights: ambient term + sum of per-light Cook-Torrance contributions.
                int n = int(u_light.directCount + 0.5);
                vec3 directSum = vec3(0.0);
                for (int i = 0; i < MAX_DIRECT_LIGHTS; i++) {
                    if (i >= n) break;
                    vec3 lDir;
                    float lAtten;
                    computeLightContribution(i, v_viewPosition, lDir, lAtten);
                    vec3 lColor = u_light.lights[i].color.rgb * u_light.lights[i].intensity * lAtten;
                    directSum += calculatePBRLoDirect(baseColor, v_normal, v_viewPosition, lDir, lColor);
                }
                vec3 litColor = calculatePBRAmbient(baseColor) + directSum;
            #else
                vec3 litColor = calculatePBRColorNoLighting(baseColor, v_normal, vec3(0.0), normalize(vec3(5.0, 5.0, 5.0)), vec3(2.0));
            #endif
        #else
            vec3 litColor = baseColor.rgb;
        #endif

        vec3 finalColor = applyTonemapping(litColor + emissive);
        f_color = vec4(finalColor, baseColor.a);
    #elif defined(USE_PHONG_MATERIAL)
        #if defined(USE_NORMALS)
            #if defined(USE_LIGHTING)
                // Direct lights: global ambient + sum of diffuse/specular per light.
                int n = int(u_light.directCount + 0.5);
                vec3 directSum = vec3(0.0);
                for (int i = 0; i < MAX_DIRECT_LIGHTS; i++) {
                    if (i >= n) break;
                    vec3 lDir;
                    float lAtten;
                    computeLightContribution(i, v_viewPosition, lDir, lAtten);
                    vec3 lColor = u_light.lights[i].color.rgb * u_light.lights[i].intensity * lAtten;
                    directSum += calculatePhongDirect(baseColor, v_normal, v_viewPosition, lDir, lColor);
                }
                vec3 phongColor = calculatePhongAmbient(baseColor) + directSum;
            #else
                vec3 phongColor = calculateBlinnPhongColorNoLighting(baseColor, v_normal, vec3(0.0), normalize(vec3(5.0, 5.0, 5.0)), vec3(2.0));
            #endif
            f_color = vec4(phongColor, baseColor.a);
        #else
            f_color = baseColor;
        #endif
    #else
        f_color = baseColor;
    #endif
}
"""
