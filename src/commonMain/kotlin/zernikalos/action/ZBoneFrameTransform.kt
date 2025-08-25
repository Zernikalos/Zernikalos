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
import zernikalos.math.ZTransform
import zernikalos.math.ZVector3
import kotlin.js.JsExport

@JsExport
@Serializable
data class ZBoneFrameTransform(
    @ProtoNumber(1)
    var position: ZVector3? = null,
    @ProtoNumber(2)
    var rotation: ZQuaternion? = null,
    @ProtoNumber(3)
    var scale: ZVector3? = null
) {
    val hasPosition: Boolean
        get() = position != null

    val hasRotation: Boolean
        get() = rotation != null

    val hasScale: Boolean
        get() = scale != null

    fun toTransform(): ZTransform {
        return ZTransform(
            position ?: ZVector3.Zero,
            rotation ?: ZQuaternion.Identity,
            scale ?: ZVector3.Ones
        )
    }

    companion object {

        fun interpolate(time: Float, prev: ZBoneFrameTransform, next: ZBoneFrameTransform): ZBoneFrameTransform {
            val result = ZBoneFrameTransform()

            if (prev.position != null && next.position != null) {
                val r = ZVector3()
                ZVector3.lerp(r, time, prev.position!!, next.position!!)
                result.position = r
            }

            if (prev.rotation != null && next.rotation != null) {
                val r = ZQuaternion()
                ZQuaternion.slerp(
                    r,
                    time,
                    prev.rotation!!,
                    next.rotation!!
                )
                result.rotation = r
            }

            if (prev.scale != null && next.scale != null) {
                val r = ZVector3()
                ZVector3.lerp(r, time, prev.scale!!, next.scale!!)
                result.scale = r
            }

            return result
        }

    }
}
