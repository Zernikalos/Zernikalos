/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ZBoundingSphereTest {

    @Test
    fun testDefaultConstructor() {
        val sphere = ZBoundingSphere()
        assertVectorEquals(ZVector3.Zero, sphere.center)
        assertEquals(0f, sphere.radius)
        assertTrue(sphere.isValid)
        assertTrue(sphere.isEmpty)
    }

    @Test
    fun testRejectsNegativeRadius() {
        assertFailsWith<IllegalArgumentException> {
            ZBoundingSphere(ZVector3.Zero, -1f)
        }
    }

    @Test
    fun testFromBox() {
        val box = ZBox3D(left = 0f, top = 0f, front = 0f, width = 2f, height = 2f, depth = 2f)
        val sphere = ZBoundingSphere(box)

        assertVectorEquals(ZVector3(1f, 1f, 1f), sphere.center)
        assertEquals(sqrt(3f), sphere.radius, epsilon)

        val filled = ZBoundingSphere()
        ZBoundingSphere.fromBox(filled, box)
        assertVectorEquals(sphere.center, filled.center)
        assertEquals(sphere.radius, filled.radius, epsilon)
    }

    @Test
    fun testContainsPoint() {
        val sphere = ZBoundingSphere(ZVector3(0f, 0f, 0f), 1f)

        assertTrue(sphere.contains(ZVector3(0f, 0f, 0f)))
        assertTrue(sphere.contains(ZVector3(1f, 0f, 0f)))
        assertFalse(sphere.contains(ZVector3(1.1f, 0f, 0f)))
        assertTrue(ZBoundingSphere.contains(sphere, ZVector3(0f, 0.5f, 0f)))
    }

    @Test
    fun testIntersectsSphere() {
        val a = ZBoundingSphere(ZVector3(0f, 0f, 0f), 1f)
        val overlapping = ZBoundingSphere(ZVector3(1.5f, 0f, 0f), 1f)
        val touching = ZBoundingSphere(ZVector3(2f, 0f, 0f), 1f)
        val separated = ZBoundingSphere(ZVector3(3f, 0f, 0f), 0.5f)

        assertTrue(a.intersects(overlapping))
        assertTrue(ZBoundingSphere.intersects(a, touching))
        assertFalse(a.intersects(separated))
    }

    @Test
    fun testIntersectsBoxDelegates() {
        val box = ZBox3D(left = 0f, top = 0f, front = 0f, width = 2f, height = 2f, depth = 2f)
        val sphere = ZBoundingSphere(ZVector3(3f, 1f, 1f), 1.1f)

        assertTrue(sphere.intersects(box))
        assertTrue(ZBoundingSphere.intersects(sphere, box))
        assertEquals(box.intersects(sphere), sphere.intersects(box))
    }

    @Test
    fun testCopy() {
        val source = ZBoundingSphere(ZVector3(1f, 2f, 3f), 4f)
        val result = ZBoundingSphere()
        ZBoundingSphere.copy(result, source)

        assertVectorEquals(source.center, result.center)
        assertEquals(4f, result.radius)
    }
}
