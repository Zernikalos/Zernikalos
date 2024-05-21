/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.uniformgenerator

import zernikalos.context.ZSceneContext
import zernikalos.math.ZAlgebraObject
import zernikalos.math.ZMatrix4
import zernikalos.objects.ZObject

class ZModelViewProjectionMatrixGenerator: ZUniformGenerator {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZAlgebraObject {
        val modelMatrixGenerator = sceneContext.getUniform("ModelMatrix")
        val viewProjectionMatrix = sceneContext.activeCamera?.viewProjectionMatrix
        if (viewProjectionMatrix == null) {
            return ZMatrix4.Identity
        }
        if (modelMatrixGenerator == null) {
            return viewProjectionMatrix
        }
        val modelMatrix = modelMatrixGenerator.compute(sceneContext, obj) as ZMatrix4
        return viewProjectionMatrix * modelMatrix
    }
}