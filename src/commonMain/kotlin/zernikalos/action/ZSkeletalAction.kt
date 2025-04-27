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
import kotlin.math.abs


@JsExport
@Serializable
class ZSkeletalAction(
    @ProtoNumber(1) val name: String
) {
    @ProtoNumber(2)
    var duration: Float = 0f

    @ProtoNumber(10)
    private val _tracks: ArrayList<ZBoneTrack> = ArrayList()

    val tracks: Array<ZBoneTrack>
        get() = _tracks.toTypedArray()

    fun addTrack(track: ZBoneTrack) {
        _tracks.add(track)
    }

    fun getKeyFrame(time: Float): ZKeyFrame {
        val kf = ZKeyFrame(time)

        for (track in tracks) {
            val position = findNearestTransform<ZPositionFrame>(
                time, track.positionTrack) { t, prev, next ->
                interpolateFrames(t, prev, next)
            }
            val rotation = findNearestTransform<ZRotationFrame>(
                time, track.rotationTrack) { t, prev, next ->
                interpolateFrames(t, prev, next)
            }
            val scale = findNearestTransform<ZScaleFrame>(
                time, track.scaleTrack) { t, prev, next ->
                interpolateFrames(t, prev, next)
            }

            val zBoneTransform = ZBoneFrameTransform(
                position = position?.position,
                rotation = rotation?.rotation,
                scale = scale?.scale
            )

            if (zBoneTransform.hasPosition || zBoneTransform.hasRotation || zBoneTransform.hasScale) {
                kf.setBoneTransform(track.boneName, zBoneTransform)
            }
        }

        return kf
    }
}

private const val EPSILON = 1e-6

/**
 * Finds and returns the appropriate transformation frame for the given time.
 *
 * @param time The target time for which to find the transformation frame
 * @param frames Array of time-based transformation frames
 * @param interpolateFrames Function that handles interpolation between two frames
 * @return The appropriate transformation frame, which could be an exact match,
 *         an interpolated frame, or null if no frames are available
 */
private inline fun <T: ZTimeFrame> findNearestTransform(
    time: Float,
    frames: Array<T>,
    interpolateFrames: (Float, T, T) -> T
): T? {
    if (frames.isEmpty()) return null

    // If there's only one frame, return it
    if (frames.size == 1) return frames[0]

    // Find the index of the frame just before the given time
    val idx = frames.indexOfLast { it.time <= time }

    return when {
        idx < 0 -> frames.first() // If the time is before the first frame
        idx >= frames.size - 1 -> frames.last() // If the time is after the last frame
        abs(frames[idx].time - time) < EPSILON -> frames[idx] // If we find the exact time
        else -> {
            val prev = frames[idx]
            val next = frames[idx + 1]
            val timeDiff = next.time - prev.time

            // Avoid division by zero
            if (timeDiff <= Float.MIN_VALUE) return prev

            val t = (time - prev.time) / timeDiff
            // Ensure t is in valid range [0,1]
            val clampedT = t.coerceIn(0f, 1f)

            try {
                interpolateFrames(clampedT, prev, next)
            } catch (e: Exception) {
                // Fallback to previous frame if interpolation fails
                prev
            }
        }
    }
}



private fun interpolateFrames(t: Float, prev: ZPositionFrame, next: ZPositionFrame): ZPositionFrame {
    return ZPositionFrame(
        time = prev.time + (next.time - prev.time) * t,
        position = ZVector3.lerp(t, prev.position, next.position)
    )
}

private fun interpolateFrames(t: Float, prev: ZRotationFrame, next: ZRotationFrame): ZRotationFrame {
    return ZRotationFrame(
        time = prev.time + (next.time - prev.time) * t,
        rotation = ZQuaternion.slerp(t, prev.rotation, next.rotation)
    )
}

private fun interpolateFrames(t: Float, prev: ZScaleFrame, next: ZScaleFrame): ZScaleFrame {
    return ZScaleFrame(
        time = prev.time + (next.time - prev.time) * t,
        scale = ZVector3.lerp(t, prev.scale, next.scale)
    )
}
