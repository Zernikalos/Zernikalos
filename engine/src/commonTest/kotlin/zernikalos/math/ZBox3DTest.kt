/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ZBox3DTest {

    @Test
    fun testDefaultConstructor() {
        val box = ZBox3D()
        assertEquals(0f, box.left)
        assertEquals(0f, box.top)
        assertEquals(0f, box.front)
        assertEquals(0f, box.width)
        assertEquals(0f, box.height)
        assertEquals(0f, box.depth)
    }

    @Test
    fun testMinMaxConstructorMapsAxes() {
        val box = ZBox3D(ZVector3(1f, 2f, 3f), ZVector3(4f, 6f, 8f))

        assertEquals(1f, box.left)
        assertEquals(2f, box.top)
        assertEquals(3f, box.front)
        assertEquals(3f, box.width)
        assertEquals(4f, box.height)
        assertEquals(5f, box.depth)

        assertVectorEquals(ZVector3(1f, 2f, 3f), box.min)
        assertVectorEquals(ZVector3(4f, 6f, 8f), box.max)
        assertVectorEquals(ZVector3(2.5f, 4f, 5.5f), box.center)
    }

    @Test
    fun testMinMaxConstructorRejectsInvertedBounds() {
        assertFailsWith<IllegalArgumentException> {
            ZBox3D(ZVector3(2f, 0f, 0f), ZVector3(1f, 1f, 1f))
        }
    }

    @Test
    fun testContainsPoint() {
        val box = ZBox3D(left = 0f, top = 0f, front = 0f, width = 2f, height = 2f, depth = 2f)

        assertTrue(box.contains(ZVector3(0f, 0f, 0f)))
        assertTrue(box.contains(ZVector3(1f, 1f, 1f)))
        assertTrue(box.contains(ZVector3(2f, 2f, 2f)))
        assertFalse(box.contains(ZVector3(-0.1f, 1f, 1f)))
        assertFalse(box.contains(ZVector3(1f, 2.1f, 1f)))
        assertTrue(ZBox3D.contains(box, ZVector3(0.5f, 0.5f, 0.5f)))
    }

    @Test
    fun testIntersectsBox() {
        val a = ZBox3D(left = 0f, top = 0f, front = 0f, width = 2f, height = 2f, depth = 2f)
        val overlapping = ZBox3D(left = 1f, top = 1f, front = 1f, width = 2f, height = 2f, depth = 2f)
        val touching = ZBox3D(left = 2f, top = 0f, front = 0f, width = 1f, height = 1f, depth = 1f)
        val separated = ZBox3D(left = 3f, top = 0f, front = 0f, width = 1f, height = 1f, depth = 1f)

        assertTrue(a.intersects(overlapping))
        assertTrue(ZBox3D.intersects(a, overlapping))
        // Strict inequalities: edge-touching boxes do not count as intersecting
        assertFalse(a.intersects(touching))
        assertFalse(a.intersects(separated))
    }

    @Test
    fun testIntersectsSphere() {
        val box = ZBox3D(left = 0f, top = 0f, front = 0f, width = 2f, height = 2f, depth = 2f)
        val inside = ZSphere(ZVector3(1f, 1f, 1f), 0.1f)
        val overlappingCorner = ZSphere(ZVector3(3f, 1f, 1f), 1.1f)
        val outside = ZSphere(ZVector3(5f, 5f, 5f), 1f)

        assertTrue(box.intersects(inside))
        assertTrue(ZBox3D.intersects(box, overlappingCorner))
        assertFalse(box.intersects(outside))
    }

    @Test
    fun testCopy() {
        val source = ZBox3D(left = 1f, top = 2f, front = 3f, width = 4f, height = 5f, depth = 6f)
        val result = ZBox3D()
        ZBox3D.copy(result, source)

        assertEquals(1f, result.left)
        assertEquals(2f, result.top)
        assertEquals(3f, result.front)
        assertEquals(4f, result.width)
        assertEquals(5f, result.height)
        assertEquals(6f, result.depth)
    }

    @Test
    fun testProtobufRoundTripUsesMinMax() {
        val original = ZBox3D(left = 1f, top = 2f, front = 3f, width = 4f, height = 5f, depth = 6f)
        val bytes = ProtoBuf.encodeToByteArray(original)
        val restored = ProtoBuf.decodeFromByteArray<ZBox3D>(bytes)

        assertVectorEquals(original.min, restored.min)
        assertVectorEquals(original.max, restored.max)
        assertEquals(original.width, restored.width)
        assertEquals(original.height, restored.height)
        assertEquals(original.depth, restored.depth)
    }
}
