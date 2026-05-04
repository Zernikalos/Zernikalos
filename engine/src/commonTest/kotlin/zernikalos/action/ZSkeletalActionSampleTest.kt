/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.action

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import zernikalos.math.ZVector3

class ZSkeletalActionSampleTest {

    @Test
    fun sampleAt_isPureAndInterpolatesPosition() {
        val track = ZBoneTrack(boneId = "joint1")
        track.addPositionFrame(ZPositionFrame(0f, ZVector3(0f, 0f, 0f)))
        track.addPositionFrame(ZPositionFrame(1f, ZVector3(10f, 0f, 0f)))
        val clip = ZSkeletalAction("clip").apply {
            duration = 1f
            addTrack(track)
        }

        val mid = clip.sampleAt(0.5f)
        val pos = assertNotNull(mid.getBoneTransform("joint1")?.position)
        assertEquals(5f, pos.x, 1e-4f)
        assertEquals(0f, pos.y, 1e-4f)
        assertEquals(0f, pos.z, 1e-4f)

        val start = clip.sampleAt(0f)
        assertTrue(start.pose.isEmpty() || start.getBoneTransform("joint1")?.position?.x == 0f)
    }
}
