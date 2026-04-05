/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.uniformgenerator

import zernikalos.context.ZSceneContext
import zernikalos.objects.ZLight
import zernikalos.search.findAllDirectLights

/** Max directional/point/spot lights sent to the GPU (ambient excluded). Must match shader `MAX_DIRECT_LIGHTS`. */
const val MAX_DIRECT_LIGHTS = 4

/** Floats per `DirectLight` in the packed `lights` blob (3× vec4 + 6 scalars). Must match shader struct fields. */
internal const val DIRECT_LIGHT_FLOAT_COUNT = 20

internal fun collectDirectLights(sceneContext: ZSceneContext): List<ZLight> {
    val scene = sceneContext.scene ?: return emptyList()
    return findAllDirectLights(scene).take(MAX_DIRECT_LIGHTS)
}
