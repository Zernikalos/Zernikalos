/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.uniformgenerator

import zernikalos.math.ZColor
import zernikalos.math.ZScalar

/**
 * Ambient light color generator. When no ambient light is registered, returns black.
 */
val ZAmbientLightColorGenerator: ZUniformGenerator = { sceneContext, _ ->
    sceneContext.activeAmbientLight?.color ?: ZColor.BLACK
}

/**
 * Ambient intensity (`AmbientLight.intensity` in shaders).
 * When no ambient [zernikalos.objects.ZLight] is registered, use 0.0 so direct lights dominate.
 */
val ZAmbientLightParamsGenerator: ZUniformGenerator = { sceneContext, _ ->
    ZScalar(sceneContext.activeAmbientLight?.intensity ?: 0.0f)
}
