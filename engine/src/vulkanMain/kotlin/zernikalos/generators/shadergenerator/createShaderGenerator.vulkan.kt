/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator

import zernikalos.components.shader.UNIFORM_IDS
import zernikalos.components.shader.ZShaderSource

internal actual fun createShaderGenerator(type: ZShaderGeneratorType): ZShaderGenerator {
    return ZDefaultVulkanShaderGenerator()
}

private class ZDefaultVulkanShaderGenerator: ZShaderGenerator() {
    override fun buildShaderSource(enabler: ZShaderProgramParameters, source: ZShaderSource) {
        source.vulkanVertexShaderSource = buildVertexShader(enabler)
        source.vulkanFragmentShaderSource = buildFragmentShader(enabler)
    }

    private fun buildVertexShader(enabler: ZShaderProgramParameters): String = """
        #version 450

        layout(set = 0, binding = ${UNIFORM_IDS.BLOCK_SCENE_MATRIX}) uniform SceneMatrix {
            mat4 u_projMatrix;
            mat4 u_viewMatrix;
            mat4 u_mvpMatrix;
        } sceneMatrix;

        ${if (enabler.useSkinning) skinningUniforms else ""}

        ${vertexInputs(enabler)}
        ${vertexOutputs(enabler)}

        ${if (enabler.useSkinning) skinningFunction else ""}

        void main() {
            ${if (enabler.useSkinning) """
            vec4 finalPosition = calculateSkinnedPosition(a_position, a_boneIndices, a_boneWeight);
            """ else """
            vec4 finalPosition = vec4(a_position, 1.0);
            """.trimIndent()}

            gl_Position = sceneMatrix.u_mvpMatrix * finalPosition;

            ${if (enabler.useNormals) "v_normal = normalize((sceneMatrix.u_viewMatrix * vec4(a_normal, 0.0)).xyz);" else ""}
            ${if (enabler.useTextures && enabler.flipTextureY) "v_uv = vec2(a_uv.x, 1.0 - a_uv.y);" else if (enabler.useTextures) "v_uv = a_uv;" else ""}
            ${if (enabler.useColors) "v_color = a_color;" else ""}
            ${if (enabler.useLighting || enabler.usePbrMaterial) "v_viewPosition = (sceneMatrix.u_viewMatrix * finalPosition).xyz;" else ""}
        }
    """.trimIndent()

    private fun buildFragmentShader(enabler: ZShaderProgramParameters): String = """
        #version 450

        layout(location = 0) out vec4 out_color;

        ${fragmentInputs(enabler)}
        ${materialUniforms(enabler)}
        ${lightingUniforms(enabler)}
        ${lightingFunctions(enabler)}

        void main() {
            vec4 baseColor = ${baseColorExpression(enabler)};
            ${if (enabler.useLighting && enabler.useNormals) """
            vec3 normal = normalize(v_normal);
            vec3 litColor = applyLighting(baseColor.rgb, normal, v_viewPosition);
            out_color = vec4(litColor, baseColor.a);
            """ else """
            out_color = baseColor;
            """.trimIndent()}
        }
    """.trimIndent()

    private fun vertexInputs(enabler: ZShaderProgramParameters): String = buildString {
        appendLine("layout(location = 0) in vec3 a_position;")
        if (enabler.useNormals) appendLine("layout(location = 1) in vec3 a_normal;")
        if (enabler.useColors) appendLine("layout(location = 2) in vec3 a_color;")
        if (enabler.useTextures) appendLine("layout(location = 3) in vec2 a_uv;")
        if (enabler.useSkinning) {
            appendLine("layout(location = 4) in vec4 a_boneWeight;")
            appendLine("layout(location = 5) in uvec4 a_boneIndices;")
        }
    }

    private fun vertexOutputs(enabler: ZShaderProgramParameters): String = buildString {
        if (enabler.useNormals) appendLine("layout(location = 0) out vec3 v_normal;")
        if (enabler.useTextures) appendLine("layout(location = 1) out vec2 v_uv;")
        if (enabler.useColors) appendLine("layout(location = 2) out vec3 v_color;")
        if (enabler.useLighting || enabler.usePbrMaterial) appendLine("layout(location = 3) out vec3 v_viewPosition;")
    }

    private fun fragmentInputs(enabler: ZShaderProgramParameters): String = buildString {
        if (enabler.useNormals) appendLine("layout(location = 0) in vec3 v_normal;")
        if (enabler.useTextures) appendLine("layout(location = 1) in vec2 v_uv;")
        if (enabler.useColors) appendLine("layout(location = 2) in vec3 v_color;")
        if (enabler.useLighting || enabler.usePbrMaterial) appendLine("layout(location = 3) in vec3 v_viewPosition;")
    }

    private fun materialUniforms(enabler: ZShaderProgramParameters): String = buildString {
        if (enabler.usePbrMaterial) {
            appendLine(
                """
                layout(set = 0, binding = ${UNIFORM_IDS.BLOCK_PBR_MATERIAL}) uniform PbrMaterial {
                    vec4 u_pbrColor;
                    vec4 u_pbrEmissive;
                    float u_pbrEmissiveIntensity;
                    float u_pbrMetalness;
                    float u_pbrRoughness;
                } pbrMaterial;
                """.trimIndent()
            )
        }
        if (enabler.usePhongMaterial) {
            appendLine(
                """
                layout(set = 0, binding = ${UNIFORM_IDS.BLOCK_PHONG_MATERIAL}) uniform PhongMaterial {
                    vec4 u_phongAmbient;
                    vec4 u_phongDiffuse;
                    vec4 u_phongSpecular;
                    float u_phongShininess;
                } phongMaterial;
                """.trimIndent()
            )
        }
    }

    private fun lightingUniforms(enabler: ZShaderProgramParameters): String {
        if (!enabler.useLighting) return ""
        return """
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

            layout(set = 0, binding = ${UNIFORM_IDS.BLOCK_LIGHT}) uniform LightUniforms {
                DirectLight lights[MAX_DIRECT_LIGHTS];
                float directCount;
            } light;

            layout(set = 0, binding = ${UNIFORM_IDS.BLOCK_AMBIENT_LIGHT}) uniform AmbientLightUniforms {
                vec4 ambientColor;
                float ambientIntensity;
            } ambientLight;
        """.trimIndent()
    }

    private fun lightingFunctions(enabler: ZShaderProgramParameters): String {
        if (!enabler.useLighting || !enabler.useNormals) return ""
        return """
            vec3 applyLighting(vec3 baseColor, vec3 normal, vec3 fragPosition) {
                vec3 color = ambientLight.ambientColor.rgb * ambientLight.ambientIntensity * baseColor;
                int directCount = clamp(int(light.directCount), 0, MAX_DIRECT_LIGHTS);
                for (int i = 0; i < directCount; i++) {
                    DirectLight directLight = light.lights[i];
                    vec3 lightDir;
                    float attenuation = 1.0;
                    if (directLight.lampType < 0.5) {
                        lightDir = normalize(-directLight.direction.xyz);
                    } else {
                        vec3 toLight = directLight.position.xyz - fragPosition;
                        float distanceToLight = length(toLight);
                        lightDir = normalize(toLight);
                        float maxRange = max(directLight.range, 0.0001);
                        attenuation = pow(clamp(1.0 - distanceToLight / maxRange, 0.0, 1.0), max(directLight.decay, 1.0));
                    }
                    float diffuse = max(dot(normal, lightDir), 0.0);
                    color += baseColor * directLight.color.rgb * directLight.intensity * diffuse * attenuation;
                }
                return color;
            }
        """.trimIndent()
    }

    private fun baseColorExpression(enabler: ZShaderProgramParameters): String = when {
        enabler.useColors -> "vec4(v_color, 1.0)"
        enabler.usePbrMaterial -> "pbrMaterial.u_pbrColor"
        enabler.usePhongMaterial -> "phongMaterial.u_phongDiffuse"
        else -> "vec4(0.92, 0.72, 0.26, 1.0)"
    }

    private val skinningUniforms = """
        layout(set = 0, binding = ${UNIFORM_IDS.BLOCK_MODEL_SKINNING_MATRIX}) uniform ModelSkinningUniforms {
            mat4 u_modelSkinningMatrix;
            mat4 u_modelSkinningMatrixInverse;
        } modelSkinning;

        layout(set = 0, binding = ${UNIFORM_IDS.BLOCK_SKINNING_MATRIX}) uniform SkinningUniforms {
            mat4 u_bones[100];
            mat4 u_invBindMatrix[100];
        } skinning;
    """.trimIndent()

    private val skinningFunction = """
        vec4 calculateSkinnedPosition(vec3 position, uvec4 boneIndices, vec4 boneWeights) {
            vec4 posInSkeletonSpace = modelSkinning.u_modelSkinningMatrix * vec4(position, 1.0);
            vec4 skinnedPosition = vec4(0.0);
            float totalWeight = 0.0;

            for (int i = 0; i < 4; i++) {
                if (boneWeights[i] > 0.0) {
                    uint boneId = boneIndices[i];
                    mat4 skinMatrix = skinning.u_bones[boneId] * skinning.u_invBindMatrix[boneId];
                    skinnedPosition += skinMatrix * posInSkeletonSpace * boneWeights[i];
                    totalWeight += boneWeights[i];
                }
            }

            vec4 blended = totalWeight > 0.0 ? skinnedPosition / totalWeight : posInSkeletonSpace;
            return modelSkinning.u_modelSkinningMatrixInverse * blended;
        }
    """.trimIndent()
}
