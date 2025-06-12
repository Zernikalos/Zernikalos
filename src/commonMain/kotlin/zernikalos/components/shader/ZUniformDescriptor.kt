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
    const val SCENE_MATRIX = "SceneMatrix"
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
