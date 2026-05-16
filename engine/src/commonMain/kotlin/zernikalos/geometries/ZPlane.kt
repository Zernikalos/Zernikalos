/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.geometries

import kotlinx.serialization.Serializable
import zernikalos.objects.ZModel
import kotlin.js.JsExport

/**
 * Single quad on the XZ plane (Y = 0), centered at the origin, normal +Y, UVs in [0, 1].
 * Indexed triangles, 16-bit indices (all backends).
 */
@JsExport
@Serializable
class ZPlane(
    val halfExtent: Float = 0.5f,
) : ZModel() {

    init {
        GeometryMeshes.requirePositiveHalfExtent(halfExtent)
        name = "PlaneXZ"
        mesh = GeometryMeshes.buildPlaneMeshXZ(halfExtent)
    }
}
