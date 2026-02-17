/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.uniformgenerator

import zernikalos.math.ZMatrix4

val ZModelViewProjectionMatrixGenerator: ZUniformGenerator = { sceneContext, obj ->
    val modelMatrixGenerator = sceneContext.getUniform("ModelMatrix")
    val viewProjectionMatrix = sceneContext.activeCamera?.viewProjectionMatrix ?: ZMatrix4.Identity

    if (modelMatrixGenerator == null) {
        viewProjectionMatrix
    } else {
        val modelMatrix = modelMatrixGenerator(sceneContext, obj) as ZMatrix4
        viewProjectionMatrix * modelMatrix
    }
}
