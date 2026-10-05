/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.sqrt

/**
 * Bounding sphere defined by a center point and a non-negative radius.
 *
 * Useful as a cheap volume when an AABB is unnecessary or as a first-pass
 * rejection test before a tighter box intersection.
 */
@Serializable
@JsExport
class ZBoundingSphere(
    var center: ZVector3 = ZVector3.Zero,
    var radius: Float = 0f
) {

    init {
        require(radius >= 0f) { "ZSphere radius must be non-negative" }
    }

    /**
     * Builds a sphere that fully encloses [box]: center at the box center and
     * radius equal to the half-diagonal (distance from center to a corner).
     *
     * This is deterministic and cheap; it is not a minimal sphere over mesh vertices.
     */
    @JsName("initFromBox")
    constructor(box: ZBox3D) : this() {
        fromBox(this, box)
    }

    val isValid: Boolean
        get() = radius >= 0f

    /**
     * True when the sphere has zero radius (default / uninitialized empty sphere).
     */
    val isEmpty: Boolean
        get() = radius == 0f

    fun contains(point: ZVector3): Boolean {
        return contains(this, point)
    }

    @JsName("intersectsSphere")
    fun intersects(other: ZBoundingSphere): Boolean {
        return intersects(this, other)
    }

    @JsName("intersectsBox")
    fun intersects(box: ZBox3D): Boolean {
        return intersects(this, box)
    }

    companion object Op {

        fun copy(result: ZBoundingSphere, sphere: ZBoundingSphere) {
            result.center.x = sphere.center.x
            result.center.y = sphere.center.y
            result.center.z = sphere.center.z
            result.radius = sphere.radius
        }

        fun fromBox(result: ZBoundingSphere, box: ZBox3D) {
            result.center.x = box.left + box.width * 0.5f
            result.center.y = box.top + box.height * 0.5f
            result.center.z = box.front + box.depth * 0.5f
            result.radius = 0.5f * sqrt(
                box.width * box.width +
                    box.height * box.height +
                    box.depth * box.depth
            )
        }

        fun contains(sphere: ZBoundingSphere, point: ZVector3): Boolean {
            val dx = point.x - sphere.center.x
            val dy = point.y - sphere.center.y
            val dz = point.z - sphere.center.z
            return dx * dx + dy * dy + dz * dz <= sphere.radius * sphere.radius
        }

        @JsName("intersectsSphere")
        fun intersects(a: ZBoundingSphere, b: ZBoundingSphere): Boolean {
            val dx = b.center.x - a.center.x
            val dy = b.center.y - a.center.y
            val dz = b.center.z - a.center.z
            val r = a.radius + b.radius
            return dx * dx + dy * dy + dz * dz <= r * r
        }

        @JsName("intersectsBox")
        fun intersects(sphere: ZBoundingSphere, box: ZBox3D): Boolean {
            return ZBox3D.intersects(box, sphere)
        }
    }
}
