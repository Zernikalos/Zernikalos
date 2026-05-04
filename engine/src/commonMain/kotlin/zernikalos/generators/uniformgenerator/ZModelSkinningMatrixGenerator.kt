/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.uniformgenerator

import zernikalos.objects.ZModel

/**
 * Uniform generator for the mesh–skin bind matrix of a skinned model.
 *
 * Returns [zernikalos.components.skeleton.ZSkinning.modelSkinBindMatrix] (mesh local space →
 * skeleton bind space). Inverse bind joints and bone pose order are documented on [ZSkinning].
 */
val ZModelSkinningMatrixGenerator: ZUniformGenerator = { sceneContext, obj ->
    obj as ZModel
    if (!obj.hasSkeleton) {
        throw Error("Unable to compute model skinning matrix without an skeleton attached to object ${obj.name}")
    }
    val skeleton = obj.skeleton!!
    val bones = skeleton.bones
    val skinning = obj.skinning!!
    val modelSkinningMatrix = skinning.modelSkinBindMatrix
    modelSkinningMatrix
}
