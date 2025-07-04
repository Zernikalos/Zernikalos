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
}

val ZUniformProjectionMatrix: ZUniformData
    get() = ZUniformData(0, "u_projMatrix", 1, ZTypes.MAT4F)
val ZUniformViewMatrix: ZUniformData
    get() = ZUniformData(1, "u_viewMatrix", 1, ZTypes.MAT4F)
val ZUniformModelViewProjectionMatrix: ZUniformData
    get() = ZUniformData(2, "u_mvpMatrix", 1, ZTypes.MAT4F)

fun ZBonesMatrixArray(count: Int): ZUniformData {
    return ZUniformData(5,"u_bones", count, ZTypes.MAT4F)
}

fun ZInverseBindMatrixArray(count: Int): ZUniformData {
    return ZUniformData(6, "u_invBindMatrix", count, ZTypes.MAT4F)
}

val ZModelViewProjectionMatrixBlock: ZUniformBlock
    get() = ZUniformBlock(10, "u_sceneMatrixBlock", listOf(
        UNIFORM_NAMES.PROJECTION_MATRIX to ZUniformProjectionMatrix,
        UNIFORM_NAMES.VIEW_MATRIX to ZUniformViewMatrix,
        UNIFORM_NAMES.MODEL_VIEW_PROJECTION_MATRIX to ZUniformModelViewProjectionMatrix
    ))

val ZSkinningMatrixBlock: ZUniformBlock
    get() = ZUniformBlock(11, "u_skinningMatrixBlock", listOf(
        UNIFORM_NAMES.BONES to ZBonesMatrixArray(100),
        UNIFORM_NAMES.INVERSE_BIND_MATRIX to ZInverseBindMatrixArray(100)
    ))

