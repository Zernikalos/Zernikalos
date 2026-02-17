/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.action

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.math.ZQuaternion
import zernikalos.math.ZVector3
import kotlin.js.JsExport

@JsExport
@Serializable
class ZKeyFrame(@ProtoNumber(1) var time: Float) {

    val pose: Map<String, ZBoneFrameTransform>
        get() = _pose

    @ProtoNumber(10)
    private val _pose: HashMap<String, ZBoneFrameTransform> = HashMap()

    fun hasBone(boneName: String): Boolean {
        return pose.containsKey(boneName)
    }

    fun getBoneTransform(boneId: String): ZBoneFrameTransform? {
        return pose[boneId]
    }

    fun setBoneTransform(boneId: String, transform: ZBoneFrameTransform) {
        _pose[boneId] = transform
    }

    companion object {

        fun interpolate(time: Float, prev: ZKeyFrame, next: ZKeyFrame): ZKeyFrame {
            val interpolatedKeyFrame = ZKeyFrame(time)

            val allBones = prev.pose.keys.union(next.pose.keys)
            for (boneId in allBones) {
                val prevTransform = prev.getBoneTransform(boneId)
                val nextTransform = next.getBoneTransform(boneId)

                if (prevTransform != null && nextTransform != null) {
                    val interpolatedTransform = ZBoneFrameTransform.interpolate(time, prevTransform, nextTransform)
                    interpolatedKeyFrame.setBoneTransform(boneId, interpolatedTransform)
                } else if (prevTransform != null) {
                    interpolatedKeyFrame.setBoneTransform(boneId, prevTransform)
                } else if (nextTransform != null) {
                    interpolatedKeyFrame.setBoneTransform(boneId, nextTransform)
                }
            }

            return interpolatedKeyFrame
        }

    }

}

@JsExport
interface ZTimeFrame {
    val time: Float
}

@JsExport
@Serializable
data class ZPositionFrame(
    @ProtoNumber(1)
    override val time: Float,
    @ProtoNumber(10)
    val position: ZVector3
): ZTimeFrame

@JsExport
@Serializable
data class ZRotationFrame(
    @ProtoNumber(1)
    override val time: Float,
    @ProtoNumber(10)
    val rotation: ZQuaternion
): ZTimeFrame

@JsExport
@Serializable
data class ZScaleFrame(
    @ProtoNumber(1)
    override val time: Float,
    @ProtoNumber(10)
    val scale: ZVector3
): ZTimeFrame
