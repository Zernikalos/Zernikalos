/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.action

import zernikalos.math.ZMatrix4
import zernikalos.objects.ZSkeleton
import kotlin.js.JsExport
import kotlin.time.Clock.System
import kotlin.time.ExperimentalTime

/**
 * @suppress
 */
@OptIn(ExperimentalTime::class)
fun System.currentTimeMillis(): Long = System.now().toEpochMilliseconds()

/**
 * Allows playback of skeletal actions stored in [ZSkeletalAction].
 *
 * Playback is bound to a [ZSkeleton]: this class owns the clock (time, speed, loop, play state)
 * while sampling the clip and committing poses to bones are separate steps. Advance time with
 * [update] or [updateWithDeltaTime], then call [applyCurrentPose] before skinning or render.
 */
@OptIn(ExperimentalTime::class)
@JsExport
class ZActionPlayer {
    private var currentAction: ZSkeletalAction? = null

    /** Current playback time in seconds (writable only inside this player). */
    var currentTime: Float = 0f
        private set

    /** Whether the action is currently playing (writable only inside this player). */
    var isPlaying: Boolean = false
        private set
    private var playbackSpeed: Float = 1f
    private var isLooping: Boolean = false
    private var skeleton: ZSkeleton? = null

    /** Wall-clock reference for [update]; reset when starting playback or calling [resetTimer]. */
    private var lastUpdateTimeMs: Long = 0L

    /** Total duration of the current action in seconds, or 0 if none. */
    val duration: Float
        get() = currentAction?.duration ?: 0f

    /**
     * Sets the skeleton and clip to play, resets playback time to zero, and applies the pose at
     * time 0 so the skeleton matches the start of the clip immediately.
     *
     * @param skeleton Target bone hierarchy for this player.
     * @param action The skeletal action (clip) to play.
     */
    fun setAction(skeleton: ZSkeleton, action: ZSkeletalAction) {
        this.skeleton = skeleton
        currentAction = action
        currentTime = 0f
        lastUpdateTimeMs = System.currentTimeMillis()
        applyCurrentPose()
    }

    /**
     * Starts playing the current action.
     *
     * @param loop Whether the action should loop when time passes [ZSkeletalAction.duration].
     */
    fun play(loop: Boolean = false) {
        if (currentAction == null) return
        if (!isPlaying) {
            lastUpdateTimeMs = System.currentTimeMillis()
        }
        isPlaying = true
        isLooping = loop
        applyCurrentPose()
    }

    /** Pauses playback without resetting [currentTime]. */
    fun pause() {
        isPlaying = false
    }

    /**
     * Stops playback, resets time to the start of the clip, and reapplies the pose at time 0.
     */
    fun stop() {
        isPlaying = false
        currentTime = 0f
        lastUpdateTimeMs = System.currentTimeMillis()
        applyCurrentPose()
    }

    /**
     * Updates playback using elapsed wall time since the last call (seconds).
     *
     * Does not modify bone matrices; call [applyCurrentPose] afterwards so the skeleton matches
     * the new time.
     */
    fun update() {
        val currentTimeMs = System.currentTimeMillis()
        val deltaTime = (currentTimeMs - lastUpdateTimeMs) / 1000f
        lastUpdateTimeMs = currentTimeMs
        updateWithDeltaTime(deltaTime)
    }

    /**
     * Advances [currentTime] by [deltaTime] scaled by [playbackSpeed] when playing.
     * Handles loop / end-of-clip stopping. Does not write bone matrices.
     *
     * @param deltaTime Elapsed time in seconds since the last call.
     */
    private fun updateWithDeltaTime(deltaTime: Float) {
        if (!isPlaying || currentAction == null) return

        currentTime += deltaTime * playbackSpeed

        if (currentTime > currentAction!!.duration) {
            if (isLooping) {
                currentTime %= currentAction!!.duration
            } else {
                currentTime = currentAction!!.duration
                isPlaying = false
            }
        }
    }

    /**
     * Samples the active clip at [currentTime] into a [ZKeyFrame] without mutating any [ZBone].
     *
     * @return The keyframe for the current time, or `null` if there is no active clip.
     */
    fun sampleCurrent(): ZKeyFrame? =
        currentAction?.sampleAt(currentTime)

    /**
     * Commits the current sampled clip pose onto [skeleton] via [ZSkeleton.applyKeyFrame].
     * No-op if there is no active clip or no skeleton.
     */
    fun applyCurrentPose() {
        val kf = sampleCurrent() ?: return
        val sk = skeleton ?: return
        sk.applyKeyFrame(kf, ZMatrix4.Identity)
    }

    /**
     * Sets the playback speed multiplier.
     *
     * @param speed 1.0 = normal speed; 2.0 = double speed, etc.
     */
    fun setPlaybackSpeed(speed: Float) {
        playbackSpeed = speed
    }

    /**
     * Seeks to [time] (clamped to the clip range) and reapplies the pose at that time.
     *
     * @param time Time in seconds within the active clip.
     */
    fun seek(time: Float) {
        if (currentAction == null) return
        currentTime = time.coerceIn(0f, currentAction!!.duration)
        applyCurrentPose()
    }

    /**
     * @return Normalized progress in `[0, 1]` (current time divided by duration), or 0 if
     * duration is zero.
     */
    fun getProgress(): Float {
        val d = duration
        return if (d > 0) currentTime / d else 0f
    }

    /**
     * Resets the internal wall-clock baseline used by [update].
     *
     * Useful when a long pause occurred and the next [update] should not apply a large jump in
     * animation time.
     */
    fun resetTimer() {
        lastUpdateTimeMs = System.currentTimeMillis()
    }
}
