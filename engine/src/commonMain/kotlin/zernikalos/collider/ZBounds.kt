/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.collider

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.math.ZBox3D
import zernikalos.math.ZSphere
import kotlin.js.JsExport

/**
 * Local-space bounding volumes for an object.
 *
 * May hold a [box], a [sphere], or both. Absence is represented by an empty
 * volume (`box.isEmpty` / `sphere.isEmpty`), not by null.
 *
 * Examples:
 * - box only: non-empty [box], empty [sphere]
 * - sphere only: empty [box], non-empty [sphere]
 * - both: neither empty
 * - none: both empty → [isEmpty]
 */
@Serializable
@JsExport
class ZBounds(
    @ProtoNumber(1)
    var box: ZBox3D = ZBox3D(),
    @ProtoNumber(2)
    var sphere: ZSphere = ZSphere()
) {

    /** True when [box] has zero usable volume. */
    val isBoxEmpty: Boolean
        get() = box.isEmpty

    /** True when [sphere] has zero usable radius. */
    val isSphereEmpty: Boolean
        get() = sphere.isEmpty

    /**
     * True when neither volume is present (box and sphere both empty).
     */
    val isEmpty: Boolean
        get() = isBoxEmpty && isSphereEmpty
}
