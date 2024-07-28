/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.uniformgenerator

import zernikalos.ZTypes
import zernikalos.context.ZSceneContext
import zernikalos.math.ZAlgebraObject
import zernikalos.math.ZAlgebraObjectCollection
import zernikalos.math.ZMatrix4
import zernikalos.objects.ZModel
import zernikalos.objects.ZObject

class ZInverseBindMatrixGenerator: ZUniformGenerator {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZAlgebraObject {
        obj as ZModel
        if (!obj.hasSkeleton) {
            throw Error("Unable to compute bone matrices without an skeleton attached to object ${obj.name}")
        }
        val skeleton = obj.skeleton!!
        val bones = skeleton.bones

        bones.sortBy { it.idx }
        val boneMatrices = bones.map {
            it.inverseBindTransform.matrix
        }

        val boneCollection = ZAlgebraObjectCollection(ZTypes.MAT4F, bones.size)
        boneCollection.addAll(0, boneMatrices)

        return boneCollection
    }
}