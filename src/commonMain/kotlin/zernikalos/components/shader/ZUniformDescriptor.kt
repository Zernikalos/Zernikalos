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
    const val PBR_METALNESS = "PBRMetalness"
    const val PBR_ROUGHNESS = "PBRRoughness"
}

object UNIFORM_IDS {
    const val BLOCK_SCENE_MATRIX = 10
    const val BLOCK_SKINNING_MATRIX = 11
    const val BLOCK_PBR_MATERIAL = 12

    const val PROJECTION_MATRIX = 0
    const val VIEW_MATRIX = 1
    const val MODEL_VIEW_PROJECTION_MATRIX = 2

    const val BONES = 5
    const val INVERSE_BIND_MATRIX = 6

    const val PBR_COLOR = 7
    const val PBR_METALNESS = 8
    const val PBR_ROUGHNESS = 9

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

val ZUniformPBRColor: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.PBR_COLOR, "u_pbrColor", 1, ZTypes.VEC4F)

val ZUniformPBRMetalness: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.PBR_METALNESS, "u_pbrMetalness", 1, ZTypes.FLOAT)

val ZUniformPBRRoughness: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.PBR_ROUGHNESS, "u_pbrRoughness", 1, ZTypes.FLOAT)

val ZPBRMaterialBlock: ZUniformBlock
    get() = ZUniformBlock(UNIFORM_IDS.BLOCK_PBR_MATERIAL, "u_pbrMaterialBlock", listOf(
        UNIFORM_NAMES.PBR_COLOR to ZUniformPBRColor,
        UNIFORM_NAMES.PBR_METALNESS to ZUniformPBRMetalness,
        UNIFORM_NAMES.PBR_ROUGHNESS to ZUniformPBRRoughness
    ))
