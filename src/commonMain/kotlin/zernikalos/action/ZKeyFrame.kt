/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.action

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
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

    fun getBoneTransform(boneName: String): ZBoneFrameTransform? {
        return pose[boneName]
    }

    fun setBoneTransform(boneName: String, transform: ZBoneFrameTransform) {
        _pose[boneName] = transform
    }

    companion object {

        fun interpolate(time: Float, prev: ZKeyFrame, next: ZKeyFrame): ZKeyFrame {
            val interpolatedKeyFrame = ZKeyFrame(time)

            val allBones = prev.pose.keys.union(next.pose.keys)
            for (boneName in allBones) {
                val prevTransform = prev.getBoneTransform(boneName)
                val nextTransform = next.getBoneTransform(boneName)

                if (prevTransform != null && nextTransform != null) {
                    val interpolatedTransform = ZBoneFrameTransform.interpolate(time, prevTransform, nextTransform)
                    interpolatedKeyFrame.setBoneTransform(boneName, interpolatedTransform)
                } else if (prevTransform != null) {
                    interpolatedKeyFrame.setBoneTransform(boneName, prevTransform)
                } else if (nextTransform != null) {
                    interpolatedKeyFrame.setBoneTransform(boneName, nextTransform)
                }
            }

            return interpolatedKeyFrame
        }

    }

}
