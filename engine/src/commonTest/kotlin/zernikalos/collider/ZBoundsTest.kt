/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.collider

import zernikalos.math.ZBox3D
import zernikalos.math.ZBoundingSphere
import zernikalos.math.ZVector3
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ZBoundsTest {

    @Test
    fun testDefaultIsEmpty() {
        val bounds = ZBounds()
        assertTrue(bounds.isBoxEmpty)
        assertTrue(bounds.isSphereEmpty)
        assertTrue(bounds.isEmpty)
    }

    @Test
    fun testBoxOnly() {
        val bounds = ZBounds(
            box = ZBox3D(left = 0f, top = 0f, front = 0f, width = 1f, height = 1f, depth = 1f),
            sphere = ZBoundingSphere()
        )
        assertFalse(bounds.isBoxEmpty)
        assertTrue(bounds.isSphereEmpty)
        assertFalse(bounds.isEmpty)
    }

    @Test
    fun testSphereOnly() {
        val bounds = ZBounds(
            box = ZBox3D(),
            sphere = ZBoundingSphere(ZVector3.Zero, 1f)
        )
        assertTrue(bounds.isBoxEmpty)
        assertFalse(bounds.isSphereEmpty)
        assertFalse(bounds.isEmpty)
    }

    @Test
    fun testBoth() {
        val bounds = ZBounds(
            box = ZBox3D(left = 0f, top = 0f, front = 0f, width = 1f, height = 1f, depth = 1f),
            sphere = ZBoundingSphere(ZVector3.Zero, 1f)
        )
        assertFalse(bounds.isBoxEmpty)
        assertFalse(bounds.isSphereEmpty)
        assertFalse(bounds.isEmpty)
    }

    @Test
    fun testFlatBoxIsNotEmpty() {
        val flat = ZBox3D(left = 0f, top = 0f, front = 0f, width = 2f, height = 2f, depth = 0f)
        assertFalse(flat.isEmpty)
        val bounds = ZBounds(box = flat)
        assertFalse(bounds.isBoxEmpty)
        assertFalse(bounds.isEmpty)
    }
}
