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

class ZSphereTest {

    @Test
    fun testDefaultConstructor() {
        val sphere = ZSphere()
        assertVectorEquals(ZVector3.Zero, sphere.center)
        assertEquals(0f, sphere.radius)
        assertTrue(sphere.isValid)
        assertTrue(sphere.isEmpty)
    }

    @Test
    fun testRejectsNegativeRadius() {
        assertFailsWith<IllegalArgumentException> {
            ZSphere(ZVector3.Zero, -1f)
        }
    }

    @Test
    fun testFromBox() {
        val box = ZBox3D(left = 0f, top = 0f, front = 0f, width = 2f, height = 2f, depth = 2f)
        val sphere = ZSphere(box)

        assertVectorEquals(ZVector3(1f, 1f, 1f), sphere.center)
        assertEquals(sqrt(3f), sphere.radius, epsilon)

        val filled = ZSphere()
        ZSphere.fromBox(filled, box)
        assertVectorEquals(sphere.center, filled.center)
        assertEquals(sphere.radius, filled.radius, epsilon)
    }

    @Test
    fun testContainsPoint() {
        val sphere = ZSphere(ZVector3(0f, 0f, 0f), 1f)

        assertTrue(sphere.contains(ZVector3(0f, 0f, 0f)))
        assertTrue(sphere.contains(ZVector3(1f, 0f, 0f)))
        assertFalse(sphere.contains(ZVector3(1.1f, 0f, 0f)))
        assertTrue(ZSphere.contains(sphere, ZVector3(0f, 0.5f, 0f)))
    }

    @Test
    fun testIntersectsSphere() {
        val a = ZSphere(ZVector3(0f, 0f, 0f), 1f)
        val overlapping = ZSphere(ZVector3(1.5f, 0f, 0f), 1f)
        val touching = ZSphere(ZVector3(2f, 0f, 0f), 1f)
        val separated = ZSphere(ZVector3(3f, 0f, 0f), 0.5f)

        assertTrue(a.intersects(overlapping))
        assertTrue(ZSphere.intersects(a, touching))
        assertFalse(a.intersects(separated))
    }

    @Test
    fun testIntersectsBoxDelegates() {
        val box = ZBox3D(left = 0f, top = 0f, front = 0f, width = 2f, height = 2f, depth = 2f)
        val sphere = ZSphere(ZVector3(3f, 1f, 1f), 1.1f)

        assertTrue(sphere.intersects(box))
        assertTrue(ZSphere.intersects(sphere, box))
        assertEquals(box.intersects(sphere), sphere.intersects(box))
    }

    @Test
    fun testCopy() {
        val source = ZSphere(ZVector3(1f, 2f, 3f), 4f)
        val result = ZSphere()
        ZSphere.copy(result, source)

        assertVectorEquals(source.center, result.center)
        assertEquals(4f, result.radius)
    }
}
