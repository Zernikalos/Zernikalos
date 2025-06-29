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

class ZVector3Test {

    @Test
    fun testConstructors() {
        val v1 = ZVector3()
        assertVectorEquals(ZVector3(0f, 0f, 0f), v1)

        val v2 = ZVector3(1f, 2f, 3f)
        assertVectorEquals(ZVector3(1f, 2f, 3f), v2)

        val v3 = ZVector3(5f)
        assertVectorEquals(ZVector3(5f, 5f, 5f), v3)

        val v4 = ZVector4(1f, 2f, 3f, 1f)
        val v5 = ZVector3(v4)
        assertVectorEquals(ZVector3(1f, 2f, 3f), v5)
    }

    @Test
    fun testSetters() {
        val v = ZVector3()
        v.x = 1f
        v.y = 2f
        v.z = 3f
        assertVectorEquals(ZVector3(1f, 2f, 3f), v)

        v.setValues(4f, 5f, 6f)
        assertVectorEquals(ZVector3(4f, 5f, 6f), v)

        v[0] = 7f
        v[1] = 8f
        v[2] = 9f
        assertVectorEquals(ZVector3(7f, 8f, 9f), v)
    }

    @Test
    fun testAddition() {
        val v1 = ZVector3(1f, 2f, 3f)
        val v2 = ZVector3(4f, 5f, 6f)
        val result = v1 + v2
        assertVectorEquals(ZVector3(5f, 7f, 9f), result)
    }

    @Test
    fun testSubtraction() {
        val v1 = ZVector3(4f, 5f, 6f)
        val v2 = ZVector3(1f, 2f, 3f)
        val result = v1 - v2
        assertVectorEquals(ZVector3(3f, 3f, 3f), result)
    }

    @Test
    fun testDotProduct() {
        val v1 = ZVector3(1f, 2f, 3f)
        val v2 = ZVector3(4f, 5f, 6f)
        val result = v1 * v2
        assertEquals(32f, result)
    }

    @Test
    fun testScalarMultiplication() {
        val v = ZVector3(1f, 2f, 3f)
        val result = v * 2f
        assertVectorEquals(ZVector3(2f, 4f, 6f), result)
    }

    @Test
    fun testNormalization() {
        val v = ZVector3(3f, 4f, 0f)
        v.normalize()
        assertVectorEquals(ZVector3(0.6f, 0.8f, 0f), v)
    }

    @Test
    fun testCrossProduct_orthogonalVectors() {
        val v1 = ZVector3(1f, 0f, 0f)
        val v2 = ZVector3(0f, 1f, 0f)
        val result = ZVector3()
        ZVector3.cross(result, v1, v2)
        assertVectorEquals(ZVector3(0f, 0f, 1f), result)
    }

    @Test
    fun testCrossProduct_parallelVectors() {
        // Test cross product with itself (should be zero vector)
        val v3 = ZVector3(1f, 2f, 3f)
        val result = ZVector3()
        ZVector3.cross(result, v3, v3)
        assertVectorEquals(ZVector3.Zero, result)
    }

    @Test
    fun testCrossProduct_antiCommutative() {
        // Test anti-commutative property: v1 x v2 = - (v2 x v1)
        val v1 = ZVector3(1f, 0f, 0f)
        val v2 = ZVector3(0f, 1f, 0f)
        val r1 = ZVector3()
        ZVector3.cross(r1, v1, v2)
        val r2 = ZVector3()
        ZVector3.cross(r2, v2, v1)
        assertVectorEquals(r1, r2 * -1f)
    }

    @Test
    fun testCrossProduct_generalVectors() {
        // Test with more complex vectors
        val v4 = ZVector3(2f, 3f, 4f)
        val v5 = ZVector3(5f, 6f, 7f)
        val result = ZVector3()
        ZVector3.cross(result, v4, v5)
        assertVectorEquals(ZVector3(-3f, 6f, -3f), result)
    }

    @Test
    fun testLerp() {
        val v1 = ZVector3(0f, 0f, 0f)
        val v2 = ZVector3(10f, 10f, 10f)
        val result = ZVector3.lerp(0.5f, v1, v2)
        assertVectorEquals(ZVector3(5f, 5f, 5f), result)
    }

//    @Test
//    fun testRotation() {
//        val v = ZVector3(1f, 0f, 0f)
//        val q = ZQuaternion.fromAxisAngle(ZVector3(0f, 0f, 1f), 90f)
//        v.rotate(q)
//        // Rotation around Z by 90 degrees should move X to Y
//        assertTrue(v.x < 0.0001f && v.x > -0.0001f)
//        assertEquals(1f, v.y, 0.0001f)
//        assertEquals(0f, v.z, 0.0001f)
//    }
}
