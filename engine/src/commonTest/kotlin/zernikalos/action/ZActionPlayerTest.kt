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
import zernikalos.components.skeleton.ZBone
import zernikalos.objects.ZSkeleton

class ZActionPlayerTest {

    @Test
    fun update_withDeltaTimeCommitsPose() {
        val bone = ZBone().apply { id = "j" }
        val skeleton = ZSkeleton().apply { root = bone }

        val track = ZBoneTrack(boneId = "j")
        track.addPositionFrame(ZPositionFrame(0f, zernikalos.math.ZVector3(0f, 0f, 0f)))
        track.addPositionFrame(ZPositionFrame(1f, zernikalos.math.ZVector3(10f, 0f, 0f)))
        val clip = ZSkeletalAction("c").apply {
            duration = 1f
            addTrack(track)
        }

        val player = ZActionPlayer()
        player.setAction(skeleton, clip)
        player.play(loop = false)

        fun tx() = bone.poseMatrix.floatArray[12]

        assertEquals(0f, tx(), 1e-4f)
        player.update(0.5f)
        assertEquals(5f, tx(), 1e-3f)
    }
}
