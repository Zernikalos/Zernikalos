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
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Parametric ray: `point = origin + direction * t` with `t >= 0`.
 *
 * Prefer a unit-length [direction]. Call [normalize] after construction when needed.
 * Intersection helpers keep the ray parameterization (do not re-normalize after
 * transforming into another space).
 */
@Serializable
@JsExport
class ZRay(
    var origin: ZVector3 = ZVector3.Zero,
    var direction: ZVector3 = ZVector3.Zero
) {

    @JsName("initWithOriginDirection")
    constructor(origin: ZVector3, direction: ZVector3, normalizeDirection: Boolean) : this(
        origin = origin,
        direction = direction
    ) {
        if (normalizeDirection) {
            normalize()
        }
    }

    val isValid: Boolean
        get() = direction.norm2 > DIRECTION_EPSILON

    fun pointAt(distance: Float): ZVector3 {
        val result = ZVector3()
        pointAt(result, this, distance)
        return result
    }

    fun normalize() {
        normalize(this)
    }

    /**
     * Ray/AABB intersection using the slab method.
     *
     * @return non-negative entry distance, `0` when the origin is inside, or `null` on miss
     */
    @JsName("intersectBox")
    fun intersect(box: ZBox3D): Float? {
        return intersect(this, box)
    }

    /**
     * Ray/sphere intersection using the quadratic geometric solution.
     *
     * @return non-negative entry distance, `0` when the origin is inside, or `null` on miss
     */
    @JsName("intersectSphere")
    fun intersect(sphere: ZSphere): Float? {
        return intersect(this, sphere)
    }

    companion object Op {

        private const val DIRECTION_EPSILON = 1e-8f

        fun copy(result: ZRay, ray: ZRay) {
            result.origin.x = ray.origin.x
            result.origin.y = ray.origin.y
            result.origin.z = ray.origin.z
            result.direction.x = ray.direction.x
            result.direction.y = ray.direction.y
            result.direction.z = ray.direction.z
        }

        fun pointAt(result: ZVector3, ray: ZRay, distance: Float) {
            result.setValues(
                ray.origin.x + ray.direction.x * distance,
                ray.origin.y + ray.direction.y * distance,
                ray.origin.z + ray.direction.z * distance
            )
        }

        fun pointAt(ray: ZRay, distance: Float): ZVector3 {
            val result = ZVector3()
            pointAt(result, ray, distance)
            return result
        }

        fun normalize(ray: ZRay) {
            ZVector3.normalize(ray.direction, ray.direction)
        }

        /**
         * Ray/AABB intersection using the slab method.
         *
         * @return non-negative entry distance, `0` when the origin is inside, or `null` on miss
         */
        @JsName("intersectBox")
        fun intersect(ray: ZRay, box: ZBox3D): Float? {
            var tMin = 0f
            var tMax = Float.POSITIVE_INFINITY

            // X slab
            if (abs(ray.direction.x) < DIRECTION_EPSILON) {
                if (ray.origin.x < box.left || ray.origin.x > box.left + box.width) return null
            } else {
                var t1 = (box.left - ray.origin.x) / ray.direction.x
                var t2 = (box.left + box.width - ray.origin.x) / ray.direction.x
                if (t1 > t2) {
                    val tmp = t1
                    t1 = t2
                    t2 = tmp
                }
                if (t1 > tMin) tMin = t1
                if (t2 < tMax) tMax = t2
                if (tMin > tMax) return null
            }

            // Y slab
            if (abs(ray.direction.y) < DIRECTION_EPSILON) {
                if (ray.origin.y < box.top || ray.origin.y > box.top + box.height) return null
            } else {
                var t1 = (box.top - ray.origin.y) / ray.direction.y
                var t2 = (box.top + box.height - ray.origin.y) / ray.direction.y
                if (t1 > t2) {
                    val tmp = t1
                    t1 = t2
                    t2 = tmp
                }
                if (t1 > tMin) tMin = t1
                if (t2 < tMax) tMax = t2
                if (tMin > tMax) return null
            }

            // Z slab
            if (abs(ray.direction.z) < DIRECTION_EPSILON) {
                if (ray.origin.z < box.front || ray.origin.z > box.front + box.depth) return null
            } else {
                var t1 = (box.front - ray.origin.z) / ray.direction.z
                var t2 = (box.front + box.depth - ray.origin.z) / ray.direction.z
                if (t1 > t2) {
                    val tmp = t1
                    t1 = t2
                    t2 = tmp
                }
                if (t1 > tMin) tMin = t1
                if (t2 < tMax) tMax = t2
                if (tMin > tMax) return null
            }

            if (tMax < 0f) return null
            return if (tMin < 0f) 0f else tMin
        }

        /**
         * Ray/sphere intersection using the quadratic geometric solution.
         *
         * @return non-negative entry distance, `0` when the origin is inside, or `null` on miss
         */
        @JsName("intersectSphere")
        fun intersect(ray: ZRay, sphere: ZSphere): Float? {
            val dx = ray.origin.x - sphere.center.x
            val dy = ray.origin.y - sphere.center.y
            val dz = ray.origin.z - sphere.center.z

            val a = ray.direction.x * ray.direction.x +
                ray.direction.y * ray.direction.y +
                ray.direction.z * ray.direction.z
            if (a < DIRECTION_EPSILON * DIRECTION_EPSILON) {
                return null
            }

            val halfB = dx * ray.direction.x + dy * ray.direction.y + dz * ray.direction.z
            val c = dx * dx + dy * dy + dz * dz - sphere.radius * sphere.radius
            val discriminant = halfB * halfB - a * c
            if (discriminant < 0f) {
                return null
            }

            val sqrtDisc = sqrt(discriminant)
            val invA = 1f / a
            val tNear = (-halfB - sqrtDisc) * invA
            val tFar = (-halfB + sqrtDisc) * invA

            if (tFar < 0f) {
                return null
            }
            return if (tNear < 0f) 0f else tNear
        }
    }
}
