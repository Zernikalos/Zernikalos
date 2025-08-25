/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.ZTypes

object UNIFORM_NAMES {
    const val PROJECTION_MATRIX = "ProjectionMatrix"
    const val VIEW_MATRIX = "ViewMatrix"
    const val MODEL_MATRIX = "ModelMatrix"
    const val MODEL_VIEW_PROJECTION_MATRIX = "ModelViewProjectionMatrix"
    const val BONES = "Bones"
    const val BIND_MATRIX = "BindMatrix"
    const val INVERSE_BIND_MATRIX = "InverseBindMatrix"

    const val PBR_COLOR = "PBRColor"
    const val PBR_EMISSIVE = "PBREmissive"
    const val PBR_EMISSIVE_INTENSITY = "PBREmissiveIntensity"
    const val PBR_METALNESS = "PBRMetalness"
    const val PBR_ROUGHNESS = "PBRRoughness"

    const val PHONG_AMBIENT = "PhongAmbient"
    const val PHONG_DIFFUSE = "PhongDiffuse"
    const val PHONG_SPECULAR = "PhongSpecular"
    const val PHONG_SHININESS = "PhongShininess"
}

object UNIFORM_IDS {
    const val BLOCK_SCENE_MATRIX = 100
    const val BLOCK_SKINNING_MATRIX = 101
    const val BLOCK_PBR_MATERIAL = 102
    const val BLOCK_PHONG_MATERIAL = 103

    const val PROJECTION_MATRIX = 0
    const val VIEW_MATRIX = 1
    const val MODEL_VIEW_PROJECTION_MATRIX = 2

    const val BONES = 5
    const val INVERSE_BIND_MATRIX = 6

    const val PBR_COLOR = 7
    const val PBR_EMISSIVE = 7
    const val PBR_EMISSIVE_INTENSITY = 8
    const val PBR_METALNESS = 8
    const val PBR_ROUGHNESS = 9

    const val PHONG_AMBIENT = 10
    const val PHONG_DIFFUSE = 11
    const val PHONG_SPECULAR = 12
    const val PHONG_SHININESS = 13
}

val ZUniformProjectionMatrix: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.PROJECTION_MATRIX, "u_projMatrix", 1, ZTypes.MAT4F)
val ZUniformViewMatrix: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.VIEW_MATRIX, "u_viewMatrix", 1, ZTypes.MAT4F)
val ZUniformModelViewProjectionMatrix: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.MODEL_VIEW_PROJECTION_MATRIX, "u_mvpMatrix", 1, ZTypes.MAT4F)

fun ZBonesMatrixArray(count: Int): ZUniformData {
    return ZUniformData(UNIFORM_IDS.BONES, "u_bones", count, ZTypes.MAT4F)
}

fun ZInverseBindMatrixArray(count: Int): ZUniformData {
    return ZUniformData(UNIFORM_IDS.INVERSE_BIND_MATRIX, "u_invBindMatrix", count, ZTypes.MAT4F)
}

val ZModelViewProjectionMatrixBlock: ZUniformBlock
    get() = ZUniformBlock(UNIFORM_IDS.BLOCK_SCENE_MATRIX, "u_sceneMatrixBlock", listOf(
        UNIFORM_NAMES.PROJECTION_MATRIX to ZUniformProjectionMatrix,
        UNIFORM_NAMES.VIEW_MATRIX to ZUniformViewMatrix,
        UNIFORM_NAMES.MODEL_VIEW_PROJECTION_MATRIX to ZUniformModelViewProjectionMatrix
    ))

val ZSkinningMatrixBlock: ZUniformBlock
    get() = ZUniformBlock(UNIFORM_IDS.BLOCK_SKINNING_MATRIX, "u_skinningMatrixBlock", listOf(
        UNIFORM_NAMES.BONES to ZBonesMatrixArray(100),
        UNIFORM_NAMES.INVERSE_BIND_MATRIX to ZInverseBindMatrixArray(100)
    ))

val ZUniformPbrColor: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.PBR_COLOR, "u_pbrColor", 1, ZTypes.VEC4F)

val ZUniformPbrEmissive: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.PBR_EMISSIVE, "u_pbrEmissive", 1, ZTypes.VEC4F)

val ZUniformPbrEmissiveIntensity: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.PBR_EMISSIVE_INTENSITY, "u_pbrEmissiveIntensity", 1, ZTypes.FLOAT)

val ZUniformPbrMetalness: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.PBR_METALNESS, "u_pbrMetalness", 1, ZTypes.FLOAT)

val ZUniformPbrRoughness: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.PBR_ROUGHNESS, "u_pbrRoughness", 1, ZTypes.FLOAT)

val ZPbrMaterialBlock: ZUniformBlock
    get() = ZUniformBlock(UNIFORM_IDS.BLOCK_PBR_MATERIAL, "u_pbrMaterialBlock", listOf(
        UNIFORM_NAMES.PBR_COLOR to ZUniformPbrColor,
        UNIFORM_NAMES.PBR_EMISSIVE to ZUniformPbrEmissive,
        UNIFORM_NAMES.PBR_EMISSIVE_INTENSITY to ZUniformPbrEmissiveIntensity,
        UNIFORM_NAMES.PBR_METALNESS to ZUniformPbrMetalness,
        UNIFORM_NAMES.PBR_ROUGHNESS to ZUniformPbrRoughness
    ))

val ZUniformPhongAmbient: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.PHONG_AMBIENT, "u_phongAmbient", 1, ZTypes.VEC4F)

val ZUniformPhongDiffuse: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.PHONG_DIFFUSE, "u_phongDiffuse", 1, ZTypes.VEC4F)

val ZUniformPhongSpecular: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.PHONG_SPECULAR, "u_phongSpecular", 1, ZTypes.VEC4F)

val ZUniformPhongShininess: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.PHONG_SHININESS, "u_phongShininess", 1, ZTypes.FLOAT)

val ZUniformPhongMaterialBlock: ZUniformBlock
    get() = ZUniformBlock(UNIFORM_IDS.BLOCK_PHONG_MATERIAL, "u_phongMaterialBlock", listOf(
        UNIFORM_NAMES.PHONG_AMBIENT to ZUniformPhongAmbient,
        UNIFORM_NAMES.PHONG_DIFFUSE to ZUniformPhongDiffuse,
        UNIFORM_NAMES.PHONG_SPECULAR to ZUniformPhongSpecular,
        UNIFORM_NAMES.PHONG_SHININESS to ZUniformPhongShininess
    ))
