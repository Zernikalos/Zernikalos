/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.uniformgenerator

import zernikalos.math.ZScalar
import zernikalos.math.ZVoidAlgebraObject

val ZLightDecayGenerator: ZUniformGenerator = { sceneContext, _ ->
    val light = sceneContext.activeLight
    if (light == null) {
        ZVoidAlgebraObject()
    } else {
        val decay = light.pointLamp?.decay ?: light.spotLamp?.decay ?: 0f
        ZScalar(decay)
    }
}
