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
import zernikalos.math.ZQuaternion
import zernikalos.objects.ZModel
import zernikalos.objects.ZObject

class ZBoneMatrixGenerator: ZUniformGenerator {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZAlgebraObject {
        obj as ZModel
        if (!obj.hasSkeleton) {
            throw Error("Unable to compute bone matrices without an skeleton attached to object ${obj.name}")
        }
        val skeleton = obj.skeleton!!
        val bones = skeleton.bones

        bones.sortBy { it.idx }
        val boneMatrices= bones.map { it ->
            if (it.name == "mixamorig_Head") {
                val poseMat = ZMatrix4()
                ZMatrix4.fromQuaternion(poseMat, ZQuaternion(0.7071f, 0f, 0.7071f, 0f))
                val m = ZMatrix4()
                ZMatrix4.mult(m, poseMat, it.bindMatrix)
                return@map m
            } else {
                return@map it.bindMatrix
            }
        }

        val boneCollection = ZAlgebraObjectCollection(ZTypes.MAT4F, bones.size)
        boneCollection.copyAllFromIndex(0, boneMatrices)

        return boneCollection
    }
}