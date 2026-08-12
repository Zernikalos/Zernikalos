/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.uniformgenerator

import zernikalos.context.backendCorrectionMatrix
import zernikalos.math.ZMatrix4

val ZModelViewProjectionMatrixGenerator: ZUniformGenerator = { sceneContext, obj ->
    val camera = sceneContext.activeCamera
    if (camera == null) {
        ZMatrix4.Identity
    } else {
        val modelMatrix = ZModelMatrixGenerator(sceneContext, obj) as ZMatrix4
        val correctedProjection = backendCorrectionMatrix() * camera.projectionMatrix
        correctedProjection * camera.viewMatrix * modelMatrix
    }
}
