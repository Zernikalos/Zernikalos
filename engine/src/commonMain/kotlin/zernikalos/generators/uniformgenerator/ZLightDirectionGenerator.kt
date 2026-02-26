/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.uniformgenerator

import zernikalos.math.ZVector4
import zernikalos.math.ZVoidAlgebraObject

val ZLightDirectionGenerator: ZUniformGenerator = { sceneContext, _ ->
    sceneContext.activeLight?.transform?.forward?.let {
        ZVector4(it.x, it.y, it.z, 0f)
    } ?: ZVoidAlgebraObject()
}
