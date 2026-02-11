/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.uniformgenerator

import zernikalos.ZTypes
import zernikalos.math.ZAlgebraObjectCollection
import zernikalos.objects.ZModel

val ZInverseBindMatrixGenerator: ZUniformGenerator = { sceneContext, obj ->
    obj as ZModel
    if (!obj.hasSkeleton) {
        throw Error("Unable to compute bone matrices without an skeleton attached to object ${obj.name}")
    }
    val skeleton = obj.skeleton!!
    val bones = skeleton.bones

    val skinning = obj.skinning!!

    // Sort bones according to the order defined in the skinning's boneIds array
    val boneIdsList = skinning.boneIds.toList()
    val skinningInverseBindMatrices = skinning.inverseBindMatrices
    val sortedBones = bones.sortedBy { bone -> boneIdsList.indexOf(bone.id) }
    val boneMatrices = sortedBones.mapIndexed { index, bone ->
        skinningInverseBindMatrices.getOrNull(index) ?: bone.inverseBindMatrix
    }

    val boneCollection = ZAlgebraObjectCollection(ZTypes.MAT4F, bones.size)
    boneCollection.copyAll(boneMatrices)

    boneCollection
}
