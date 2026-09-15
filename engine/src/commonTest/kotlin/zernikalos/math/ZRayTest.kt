/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ZRayTest {

    @Test
    fun testPointAt() {
        val ray = ZRay(ZVector3(1f, 2f, 3f), ZVector3(1f, 0f, 0f))
        assertVectorEquals(ZVector3(4f, 2f, 3f), ray.pointAt(3f))

        val result = ZVector3()
        ZRay.pointAt(result, ray, 2f)
        assertVectorEquals(ZVector3(3f, 2f, 3f), result)
    }

    @Test
    fun testNormalize() {
        val ray = ZRay(ZVector3.Zero, ZVector3(0f, 3f, 0f), normalizeDirection = true)
        assertEquals(1f, ray.direction.norm2, epsilon)
        assertVectorEquals(ZVector3(0f, 1f, 0f), ray.direction)

        val unnormalized = ZRay(ZVector3.Zero, ZVector3(2f, 0f, 0f))
        unnormalized.normalize()
        assertVectorEquals(ZVector3(1f, 0f, 0f), unnormalized.direction)
    }

    @Test
    fun testIsValid() {
        assertFalse(ZRay().isValid)
        assertTrue(ZRay(ZVector3.Zero, ZVector3(1f, 0f, 0f)).isValid)
    }

    @Test
    fun testIntersectBoxHit() {
        val box = ZBox3D(left = 0f, top = 0f, front = 0f, width = 2f, height = 2f, depth = 2f)
        val ray = ZRay(ZVector3(-1f, 1f, 1f), ZVector3(1f, 0f, 0f))

        val t = assertNotNull(ray.intersect(box))
        assertEquals(1f, t, epsilon)
        assertVectorEquals(ZVector3(0f, 1f, 1f), ray.pointAt(t))
    }

    @Test
    fun testIntersectBoxMiss() {
        val box = ZBox3D(left = 0f, top = 0f, front = 0f, width = 2f, height = 2f, depth = 2f)
        val ray = ZRay(ZVector3(-1f, 5f, 1f), ZVector3(1f, 0f, 0f))

        assertNull(ray.intersect(box))
        assertNull(ZRay.intersect(ray, box))
    }

    @Test
    fun testIntersectBoxOriginInsideReturnsZero() {
        val box = ZBox3D(left = 0f, top = 0f, front = 0f, width = 2f, height = 2f, depth = 2f)
        val ray = ZRay(ZVector3(1f, 1f, 1f), ZVector3(1f, 0f, 0f))

        assertEquals(0f, ray.intersect(box))
    }

    @Test
    fun testIntersectBoxParallelOutsideMisses() {
        val box = ZBox3D(left = 0f, top = 0f, front = 0f, width = 2f, height = 2f, depth = 2f)
        val ray = ZRay(ZVector3(-1f, 5f, 1f), ZVector3(0f, 0f, 1f))

        assertNull(ray.intersect(box))
    }

    @Test
    fun testIntersectBoxBehindRayMisses() {
        val box = ZBox3D(left = 0f, top = 0f, front = 0f, width = 2f, height = 2f, depth = 2f)
        val ray = ZRay(ZVector3(5f, 1f, 1f), ZVector3(1f, 0f, 0f))

        assertNull(ray.intersect(box))
    }

    @Test
    fun testIntersectSphereHit() {
        val sphere = ZSphere(ZVector3(0f, 0f, 0f), 1f)
        val ray = ZRay(ZVector3(-3f, 0f, 0f), ZVector3(1f, 0f, 0f))

        val t = assertNotNull(ray.intersect(sphere))
        assertEquals(2f, t, epsilon)
        assertVectorEquals(ZVector3(-1f, 0f, 0f), ray.pointAt(t))
    }

    @Test
    fun testIntersectSphereMiss() {
        val sphere = ZSphere(ZVector3(0f, 0f, 0f), 1f)
        val ray = ZRay(ZVector3(-3f, 2f, 0f), ZVector3(1f, 0f, 0f))

        assertNull(ray.intersect(sphere))
        assertNull(ZRay.intersect(ray, sphere))
    }

    @Test
    fun testIntersectSphereOriginInsideReturnsZero() {
        val sphere = ZSphere(ZVector3(0f, 0f, 0f), 1f)
        val ray = ZRay(ZVector3(0.2f, 0f, 0f), ZVector3(1f, 0f, 0f))

        assertEquals(0f, ray.intersect(sphere))
    }

    @Test
    fun testIntersectSphereBehindRayMisses() {
        val sphere = ZSphere(ZVector3(0f, 0f, 0f), 1f)
        val ray = ZRay(ZVector3(3f, 0f, 0f), ZVector3(1f, 0f, 0f))

        assertNull(ray.intersect(sphere))
    }

    @Test
    fun testCopy() {
        val source = ZRay(ZVector3(1f, 2f, 3f), ZVector3(0f, 1f, 0f))
        val result = ZRay()
        ZRay.copy(result, source)

        assertVectorEquals(source.origin, result.origin)
        assertVectorEquals(source.direction, result.direction)
    }
}
