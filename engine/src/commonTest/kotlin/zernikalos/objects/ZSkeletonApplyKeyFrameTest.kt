/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlin.test.Test
import kotlin.test.assertEquals
import zernikalos.action.ZBoneFrameTransform
import zernikalos.action.ZKeyFrame
import zernikalos.components.skeleton.ZBone
import zernikalos.math.ZMatrix4
import zernikalos.math.ZVector3

class ZSkeletonApplyKeyFrameTest {

    @Test
    fun applyKeyFrame_replacesPositionChannelFromKeyframe() {
        val root = ZBone().apply { id = "root" }
        val skeleton = ZSkeleton().apply { this.root = root }
        val kf = ZKeyFrame(0f).apply {
            setBoneTransform("root", ZBoneFrameTransform(position = ZVector3(3f, -2f, 1f)))
        }
        skeleton.applyKeyFrame(kf, ZMatrix4.Identity)
        assertEquals(3f, root.poseMatrix.floatArray[12], 1e-4f)
        assertEquals(-2f, root.poseMatrix.floatArray[13], 1e-4f)
        assertEquals(1f, root.poseMatrix.floatArray[14], 1e-4f)
    }
}
