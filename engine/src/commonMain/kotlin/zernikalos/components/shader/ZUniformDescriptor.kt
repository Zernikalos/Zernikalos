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
data class UniformKey(val id: Int, val name: String)

object UNIFORM_KEYS {
    // Block keys (higher ids for binding)
    val BLOCK_SCENE_MATRIX = UniformKey(14, "SceneMatrix")
    val BLOCK_MODEL_SKINNING_MATRIX = UniformKey(13, "ModelSkinningUniforms")
    val BLOCK_SKINNING_MATRIX = UniformKey(15, "SkinningUniforms")
    val BLOCK_PBR_MATERIAL = UniformKey(16, "PbrMaterial")
    val BLOCK_PHONG_MATERIAL = UniformKey(17, "PhongMaterial")

    // Member keys
    val PROJECTION_MATRIX = UniformKey(0, "ProjectionMatrix")
    val VIEW_MATRIX = UniformKey(1, "ViewMatrix")
    val MODEL_MATRIX = UniformKey(-1, "ModelMatrix")
    val MODEL_VIEW_PROJECTION_MATRIX = UniformKey(2, "ModelViewProjectionMatrix")

    val MODEL_SKINNING_MATRIX = UniformKey(3, "ModelSkinningMatrix")
    val INVERSE_MODEL_SKINNING_MATRIX = UniformKey(4, "InverseModelSkinningMatrix")
    val BONES = UniformKey(5, "Bones")
    val BIND_MATRIX = UniformKey(-1, "BindMatrix")
    val INVERSE_BIND_MATRIX = UniformKey(6, "InverseBindMatrix")

    val PBR_COLOR = UniformKey(7, "PBRColor")
    val PBR_EMISSIVE = UniformKey(7, "PBREmissive")
    val PBR_EMISSIVE_INTENSITY = UniformKey(8, "PBREmissiveIntensity")
    val PBR_METALNESS = UniformKey(8, "PBRMetalness")
    val PBR_ROUGHNESS = UniformKey(9, "PBRRoughness")

    val PHONG_AMBIENT = UniformKey(10, "PhongAmbient")
    val PHONG_DIFFUSE = UniformKey(11, "PhongDiffuse")
    val PHONG_SPECULAR = UniformKey(12, "PhongSpecular")
    val PHONG_SHININESS = UniformKey(13, "PhongShininess")
}

/** @deprecated Use UNIFORM_KEYS.BLOCK_*.id - Kept for shader binding references (WGSL/Metal). */
object UNIFORM_IDS {
    val BLOCK_SCENE_MATRIX get() = UNIFORM_KEYS.BLOCK_SCENE_MATRIX.id
    val BLOCK_MODEL_SKINNING_MATRIX get() = UNIFORM_KEYS.BLOCK_MODEL_SKINNING_MATRIX.id
    val BLOCK_SKINNING_MATRIX get() = UNIFORM_KEYS.BLOCK_SKINNING_MATRIX.id
    val BLOCK_PBR_MATERIAL get() = UNIFORM_KEYS.BLOCK_PBR_MATERIAL.id
    val BLOCK_PHONG_MATERIAL get() = UNIFORM_KEYS.BLOCK_PHONG_MATERIAL.id
}

/** Declarative definition of the scene matrix uniform block. */
object SceneMatrixUniforms : UniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_SCENE_MATRIX,
    glslName = "u_sceneMatrixBlock",
    members = listOf(
        UniformMember(UNIFORM_KEYS.PROJECTION_MATRIX, ZTypes.MAT4F, count = 1, glslName = "u_projMatrix"),
        UniformMember(UNIFORM_KEYS.VIEW_MATRIX, ZTypes.MAT4F, count = 1, glslName = "u_viewMatrix"),
        UniformMember(UNIFORM_KEYS.MODEL_VIEW_PROJECTION_MATRIX, ZTypes.MAT4F, count = 1, glslName = "u_mvpMatrix")
    ),
    generators = mapOf(
        UNIFORM_KEYS.PROJECTION_MATRIX.name to ZProjectionMatrixGenerator,
        UNIFORM_KEYS.VIEW_MATRIX.name to ZViewMatrixGenerator,
        UNIFORM_KEYS.MODEL_VIEW_PROJECTION_MATRIX.name to ZModelViewProjectionMatrixGenerator
    )
)

val ZModelViewProjectionMatrixBlock: ZUniform
    get() = SceneMatrixUniforms.toZUniform()

/** Declarative definition of the skinning uniform block. Single source for layout and generators. */
object SkinningUniforms : UniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_SKINNING_MATRIX,
    glslName = "u_skinningMatrixBlock",
    members = listOf(
        UniformMember(UNIFORM_KEYS.BONES, ZTypes.MAT4F, count = 100, glslName = "u_bones"),
        UniformMember(UNIFORM_KEYS.INVERSE_BIND_MATRIX, ZTypes.MAT4F, count = 100, glslName = "u_invBindMatrix")
    ),
    generators = mapOf(
        UNIFORM_KEYS.BONES.name to ZBoneMatrixGenerator,
        UNIFORM_KEYS.INVERSE_BIND_MATRIX.name to ZInverseBindMatrixGenerator
    )
)

val ZSkinningMatrixBlock: ZUniform
    get() = SkinningUniforms.toZUniform()

/** Declarative definition of the model skinning uniform block. */
object ModelSkinningUniforms : UniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_MODEL_SKINNING_MATRIX,
    glslName = "u_modelSkinningMatrixBlock",
    members = listOf(
        UniformMember(UNIFORM_KEYS.MODEL_SKINNING_MATRIX, ZTypes.MAT4F, count = 1, glslName = "u_modelSkinningMatrix"),
        UniformMember(UNIFORM_KEYS.INVERSE_MODEL_SKINNING_MATRIX, ZTypes.MAT4F, count = 1, glslName = "u_modelSkinningMatrixInverse")
    ),
    generators = mapOf(
        UNIFORM_KEYS.MODEL_SKINNING_MATRIX.name to ZModelSkinningMatrixGenerator,
        UNIFORM_KEYS.INVERSE_MODEL_SKINNING_MATRIX.name to ZInverseModelSkinningMatrixGenerator
    )
)

val ZModelSkinningMatrixBlock: ZUniform
    get() = ModelSkinningUniforms.toZUniform()

/** Declarative definition of the PBR material uniform block. */
object PbrMaterialUniforms : UniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_PBR_MATERIAL,
    glslName = "u_pbrMaterialBlock",
    members = listOf(
        UniformMember(UNIFORM_KEYS.PBR_COLOR, ZTypes.VEC4F, count = 1, glslName = "u_pbrColor"),
        UniformMember(UNIFORM_KEYS.PBR_EMISSIVE, ZTypes.VEC4F, count = 1, glslName = "u_pbrEmissive"),
        UniformMember(UNIFORM_KEYS.PBR_EMISSIVE_INTENSITY, ZTypes.FLOAT, count = 1, glslName = "u_pbrEmissiveIntensity"),
        UniformMember(UNIFORM_KEYS.PBR_METALNESS, ZTypes.FLOAT, count = 1, glslName = "u_pbrMetalness"),
        UniformMember(UNIFORM_KEYS.PBR_ROUGHNESS, ZTypes.FLOAT, count = 1, glslName = "u_pbrRoughness")
    ),
    generators = mapOf(
        UNIFORM_KEYS.PBR_COLOR.name to ZPbrColorGenerator,
        UNIFORM_KEYS.PBR_EMISSIVE.name to ZPbrEmissiveGenerator,
        UNIFORM_KEYS.PBR_EMISSIVE_INTENSITY.name to ZPbrEmissiveIntensityGenerator,
        UNIFORM_KEYS.PBR_METALNESS.name to ZPbrMetalnessGenerator,
        UNIFORM_KEYS.PBR_ROUGHNESS.name to ZPbrRoughnessGenerator
    )
)

val ZPbrMaterialBlock: ZUniform
    get() = PbrMaterialUniforms.toZUniform()

/** Declarative definition of the Phong material uniform block. */
object PhongMaterialUniforms : UniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_PHONG_MATERIAL,
    glslName = "u_phongMaterialBlock",
    members = listOf(
        UniformMember(UNIFORM_KEYS.PHONG_AMBIENT, ZTypes.VEC4F, count = 1, glslName = "u_phongAmbient"),
        UniformMember(UNIFORM_KEYS.PHONG_DIFFUSE, ZTypes.VEC4F, count = 1, glslName = "u_phongDiffuse"),
        UniformMember(UNIFORM_KEYS.PHONG_SPECULAR, ZTypes.VEC4F, count = 1, glslName = "u_phongSpecular"),
        UniformMember(UNIFORM_KEYS.PHONG_SHININESS, ZTypes.FLOAT, count = 1, glslName = "u_phongShininess")
    ),
    generators = mapOf(
        UNIFORM_KEYS.PHONG_AMBIENT.name to ZPhongAmbientGenerator,
        UNIFORM_KEYS.PHONG_DIFFUSE.name to ZPhongDiffuseGenerator,
        UNIFORM_KEYS.PHONG_SPECULAR.name to ZPhongSpecularGenerator,
        UNIFORM_KEYS.PHONG_SHININESS.name to ZPhongShininessGenerator
    )
)

val ZUniformPhongMaterialBlock: ZUniform
    get() = PhongMaterialUniforms.toZUniform()

