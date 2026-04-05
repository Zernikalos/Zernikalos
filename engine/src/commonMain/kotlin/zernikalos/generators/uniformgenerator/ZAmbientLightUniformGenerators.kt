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
import zernikalos.search.findAmbientLight

/**
 * Ambient light color generator. When no enabled ambient light exists in the scene, returns black.
 */
val ZAmbientLightColorGenerator: ZUniformGenerator = { sceneContext, _ ->
    val scene = sceneContext.scene
    if (scene == null) {
        ZColor.BLACK
    } else {
        findAmbientLight(scene)?.color ?: ZColor.BLACK
    }
}

/**
 * Ambient intensity (`AmbientLight.intensity` in shaders).
 * When no enabled ambient [zernikalos.objects.ZLight] exists in the scene, use 0.0 so direct lights dominate.
 */
val ZAmbientLightParamsGenerator: ZUniformGenerator = { sceneContext, _ ->
    val scene = sceneContext.scene
    val intensity = if (scene == null) {
        0.0f
    } else {
        findAmbientLight(scene)?.intensity ?: 0.0f
    }
    ZScalar(intensity)
}
