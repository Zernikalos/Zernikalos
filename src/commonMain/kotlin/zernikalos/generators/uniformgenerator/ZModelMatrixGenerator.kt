/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.uniformgenerator

import zernikalos.context.ZSceneContext
import zernikalos.math.ZAlgebraObject
import zernikalos.math.ZMatrix4
import zernikalos.objects.ZObject
import zernikalos.upToRoot

class ZModelMatrixGenerator: ZUniformGenerator {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZAlgebraObject {
        var m = ZMatrix4.Identity
        for (parentObj in upToRoot(obj)) {
            val parentMatrix = parentObj.transform.matrix
            m = parentMatrix * m
        }
        return m
    }
}