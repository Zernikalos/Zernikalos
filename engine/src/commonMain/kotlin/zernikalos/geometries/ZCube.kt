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
 * Axis-aligned cube centered at the origin, edge length `2 * halfExtent`, with per-face normals and UVs.
 * Indexed triangles, 16-bit indices (all backends).
 */
@JsExport
@Serializable
class ZCube(
    val halfExtent: Float = 0.5f,
) : ZModel() {

    init {
        GeometryMeshes.requirePositiveHalfExtent(halfExtent)
        name = "Cube"
        mesh = GeometryMeshes.buildCubeMesh(halfExtent)
    }
}
