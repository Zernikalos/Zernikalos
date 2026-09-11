/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.ZTypes
import zernikalos.generators.uniformgenerator.*

/**
 * Unified key for a uniform: holds both id and name in a single source of truth.
 */
data class ZUniformKey(val id: Int, val name: String)

object UNIFORM_KEYS {
    // Block keys (higher ids for binding)
    val BLOCK_SCENE_MATRIX = ZUniformKey(14, "SceneMatrix")
    val BLOCK_MODEL_SKINNING_MATRIX = ZUniformKey(13, "ModelSkinningUniforms")
    val BLOCK_SKINNING_MATRIX = ZUniformKey(15, "SkinningUniforms")
    val BLOCK_PBR_MATERIAL = ZUniformKey(16, "PbrMaterial")
    val BLOCK_PHONG_MATERIAL = ZUniformKey(17, "PhongMaterial")
    val BLOCK_LIGHT = ZUniformKey(18, "LightUniforms")
    val BLOCK_AMBIENT_LIGHT = ZUniformKey(19, "AmbientLightUniforms")

    // Member keys
    val PROJECTION_MATRIX = ZUniformKey(0, "ProjectionMatrix")
    val VIEW_MATRIX = ZUniformKey(1, "ViewMatrix")
    val MODEL_MATRIX = ZUniformKey(-1, "ModelMatrix")
    val MODEL_VIEW_PROJECTION_MATRIX = ZUniformKey(2, "ModelViewProjectionMatrix")

    val MODEL_SKINNING_MATRIX = ZUniformKey(3, "ModelSkinningMatrix")
    val INVERSE_MODEL_SKINNING_MATRIX = ZUniformKey(4, "InverseModelSkinningMatrix")
    val BONES = ZUniformKey(5, "Bones")
    val BIND_MATRIX = ZUniformKey(-1, "BindMatrix")
    val INVERSE_BIND_MATRIX = ZUniformKey(6, "InverseBindMatrix")

    val PBR_COLOR = ZUniformKey(7, "PBRColor")
    val PBR_EMISSIVE = ZUniformKey(7, "PBREmissive")
    val PBR_EMISSIVE_INTENSITY = ZUniformKey(8, "PBREmissiveIntensity")
    val PBR_METALNESS = ZUniformKey(8, "PBRMetalness")
    val PBR_ROUGHNESS = ZUniformKey(9, "PBRRoughness")

    val PHONG_AMBIENT = ZUniformKey(10, "PhongAmbient")
    val PHONG_DIFFUSE = ZUniformKey(11, "PhongDiffuse")
    val PHONG_SPECULAR = ZUniformKey(12, "PhongSpecular")
    val PHONG_SHININESS = ZUniformKey(13, "PhongShininess")

    /** Packed `DirectLight lights[]` blob; size = [MAX_DIRECT_LIGHTS] * [DIRECT_LIGHT_FLOAT_COUNT] floats. */
    val DIRECT_LIGHTS = ZUniformKey(20, "DirectLights")
    val LIGHT_DIRECT_COUNT = ZUniformKey(25, "LightDirectCount")
    /** `AmbientLight.color` in the lighting UBO. */
    val AMBIENT_LIGHT_COLOR = ZUniformKey(29, "AmbientLightColor")
    /** `AmbientLight.intensity` in the lighting UBO. */
    val AMBIENT_LIGHT_PARAMS = ZUniformKey(30, "AmbientLightParams")
}

/** @deprecated Use UNIFORM_KEYS.BLOCK_*.id - Kept for shader binding references (WGSL/Metal). */
object UNIFORM_IDS {
    val BLOCK_SCENE_MATRIX get() = UNIFORM_KEYS.BLOCK_SCENE_MATRIX.id
    val BLOCK_MODEL_SKINNING_MATRIX get() = UNIFORM_KEYS.BLOCK_MODEL_SKINNING_MATRIX.id
    val BLOCK_SKINNING_MATRIX get() = UNIFORM_KEYS.BLOCK_SKINNING_MATRIX.id
    val BLOCK_PBR_MATERIAL get() = UNIFORM_KEYS.BLOCK_PBR_MATERIAL.id
    val BLOCK_PHONG_MATERIAL get() = UNIFORM_KEYS.BLOCK_PHONG_MATERIAL.id
    val BLOCK_LIGHT get() = UNIFORM_KEYS.BLOCK_LIGHT.id
    val BLOCK_AMBIENT_LIGHT get() = UNIFORM_KEYS.BLOCK_AMBIENT_LIGHT.id
}

/** Declarative definition of the scene matrix uniform block. */
object SceneMatrixUniforms : ZUniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_SCENE_MATRIX,
    shaderName = "u_sceneMatrixBlock",
    members = listOf(
        ZUniformMember(UNIFORM_KEYS.PROJECTION_MATRIX, ZTypes.MAT4F, count = 1, shaderName = "u_projMatrix"),
        ZUniformMember(UNIFORM_KEYS.VIEW_MATRIX, ZTypes.MAT4F, count = 1, shaderName = "u_viewMatrix"),
        ZUniformMember(UNIFORM_KEYS.MODEL_VIEW_PROJECTION_MATRIX, ZTypes.MAT4F, count = 1, shaderName = "u_mvpMatrix")
    ),
    generators = mapOf(
        UNIFORM_KEYS.PROJECTION_MATRIX to ZProjectionMatrixGenerator,
        UNIFORM_KEYS.VIEW_MATRIX to ZViewMatrixGenerator,
        UNIFORM_KEYS.MODEL_VIEW_PROJECTION_MATRIX to ZModelViewProjectionMatrixGenerator
    )
)

val ZModelViewProjectionMatrixBlock: ZUniform
    get() = SceneMatrixUniforms.toZUniform()

/** Declarative definition of the skinning uniform block. Single source for layout and generators. */
object SkinningUniforms : ZUniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_SKINNING_MATRIX,
    shaderName = "u_skinningMatrixBlock",
    members = listOf(
        ZUniformMember(UNIFORM_KEYS.BONES, ZTypes.MAT4F, count = 100, shaderName = "u_bones"),
        ZUniformMember(UNIFORM_KEYS.INVERSE_BIND_MATRIX, ZTypes.MAT4F, count = 100, shaderName = "u_invBindMatrix")
    ),
    generators = mapOf(
        UNIFORM_KEYS.BONES to ZBoneMatrixGenerator,
        UNIFORM_KEYS.INVERSE_BIND_MATRIX to ZInverseBindMatrixGenerator
    )
)

val ZSkinningMatrixBlock: ZUniform
    get() = SkinningUniforms.toZUniform()

/** Declarative definition of the model skinning uniform block. */
object ModelSkinningUniforms : ZUniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_MODEL_SKINNING_MATRIX,
    shaderName = "u_modelSkinningMatrixBlock",
    members = listOf(
        ZUniformMember(UNIFORM_KEYS.MODEL_SKINNING_MATRIX, ZTypes.MAT4F, count = 1, shaderName = "u_modelSkinningMatrix"),
        ZUniformMember(UNIFORM_KEYS.INVERSE_MODEL_SKINNING_MATRIX, ZTypes.MAT4F, count = 1, shaderName = "u_modelSkinningMatrixInverse")
    ),
    generators = mapOf(
        UNIFORM_KEYS.MODEL_SKINNING_MATRIX to ZModelSkinningMatrixGenerator,
        UNIFORM_KEYS.INVERSE_MODEL_SKINNING_MATRIX to ZInverseModelSkinningMatrixGenerator
    )
)

val ZModelSkinningMatrixBlock: ZUniform
    get() = ModelSkinningUniforms.toZUniform()

/** Declarative definition of the PBR material uniform block. */
object PbrMaterialUniforms : ZUniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_PBR_MATERIAL,
    shaderName = "u_pbrMaterialBlock",
    members = listOf(
        ZUniformMember(UNIFORM_KEYS.PBR_COLOR, ZTypes.VEC4F, count = 1, shaderName = "u_pbrColor"),
        ZUniformMember(UNIFORM_KEYS.PBR_EMISSIVE, ZTypes.VEC4F, count = 1, shaderName = "u_pbrEmissive"),
        ZUniformMember(UNIFORM_KEYS.PBR_EMISSIVE_INTENSITY, ZTypes.FLOAT, count = 1, shaderName = "u_pbrEmissiveIntensity"),
        ZUniformMember(UNIFORM_KEYS.PBR_METALNESS, ZTypes.FLOAT, count = 1, shaderName = "u_pbrMetalness"),
        ZUniformMember(UNIFORM_KEYS.PBR_ROUGHNESS, ZTypes.FLOAT, count = 1, shaderName = "u_pbrRoughness")
    ),
    generators = mapOf(
        UNIFORM_KEYS.PBR_COLOR to ZPbrColorGenerator,
        UNIFORM_KEYS.PBR_EMISSIVE to ZPbrEmissiveGenerator,
        UNIFORM_KEYS.PBR_EMISSIVE_INTENSITY to ZPbrEmissiveIntensityGenerator,
        UNIFORM_KEYS.PBR_METALNESS to ZPbrMetalnessGenerator,
        UNIFORM_KEYS.PBR_ROUGHNESS to ZPbrRoughnessGenerator
    )
)

val ZPbrMaterialBlock: ZUniform
    get() = PbrMaterialUniforms.toZUniform()

/** Declarative definition of the Phong material uniform block. */
object PhongMaterialUniforms : ZUniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_PHONG_MATERIAL,
    shaderName = "u_phongMaterialBlock",
    members = listOf(
        ZUniformMember(UNIFORM_KEYS.PHONG_AMBIENT, ZTypes.VEC4F, count = 1, shaderName = "u_phongAmbient"),
        ZUniformMember(UNIFORM_KEYS.PHONG_DIFFUSE, ZTypes.VEC4F, count = 1, shaderName = "u_phongDiffuse"),
        ZUniformMember(UNIFORM_KEYS.PHONG_SPECULAR, ZTypes.VEC4F, count = 1, shaderName = "u_phongSpecular"),
        ZUniformMember(UNIFORM_KEYS.PHONG_SHININESS, ZTypes.FLOAT, count = 1, shaderName = "u_phongShininess")
    ),
    generators = mapOf(
        UNIFORM_KEYS.PHONG_AMBIENT to ZPhongAmbientGenerator,
        UNIFORM_KEYS.PHONG_DIFFUSE to ZPhongDiffuseGenerator,
        UNIFORM_KEYS.PHONG_SPECULAR to ZPhongSpecularGenerator,
        UNIFORM_KEYS.PHONG_SHININESS to ZPhongShininessGenerator
    )
)

val ZUniformPhongMaterialBlock: ZUniform
    get() = PhongMaterialUniforms.toZUniform()

/** Declarative definition of the light uniform block (direct lights only). */
object LightUniformsDef : ZUniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_LIGHT,
    shaderName = "u_lightBlock",
    members = listOf(
        ZUniformMember(
            UNIFORM_KEYS.DIRECT_LIGHTS,
            ZTypes.FLOAT,
            count = MAX_DIRECT_LIGHTS * DIRECT_LIGHT_FLOAT_COUNT,
            shaderName = "lights"
        ),
        ZUniformMember(UNIFORM_KEYS.LIGHT_DIRECT_COUNT, ZTypes.FLOAT, count = 1, shaderName = "directCount")
    ),
    generators = mapOf(
        UNIFORM_KEYS.DIRECT_LIGHTS to ZDirectLightsArrayGenerator,
        UNIFORM_KEYS.LIGHT_DIRECT_COUNT to ZLightDirectCountGenerator
    )
)

val ZLightUniformBlock: ZUniform
    get() = LightUniformsDef.toZUniform()

/** Declarative definition of the ambient light uniform block. */
object AmbientLightUniforms : ZUniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_AMBIENT_LIGHT,
    shaderName = "u_ambientLightBlock",
    members = listOf(
        ZUniformMember(UNIFORM_KEYS.AMBIENT_LIGHT_COLOR, ZTypes.VEC4F, count = 1, shaderName = "ambientColor"),
        ZUniformMember(UNIFORM_KEYS.AMBIENT_LIGHT_PARAMS, ZTypes.FLOAT, count = 1, shaderName = "ambientIntensity")
    ),
    generators = mapOf(
        UNIFORM_KEYS.AMBIENT_LIGHT_COLOR to ZAmbientLightColorGenerator,
        UNIFORM_KEYS.AMBIENT_LIGHT_PARAMS to ZAmbientLightParamsGenerator
    )
)

val ZAmbientLightUniformBlock: ZUniform
    get() = AmbientLightUniforms.toZUniform()

