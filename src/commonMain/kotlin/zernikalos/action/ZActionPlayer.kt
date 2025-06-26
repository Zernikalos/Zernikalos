package zernikalos.action

import zernikalos.math.ZMatrix4
import zernikalos.objects.ZModel
import kotlin.js.JsExport
import kotlin.time.Clock.System
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun System.currentTimeMillis(): Long = System.now().toEpochMilliseconds()

/**
 * Class that allows playback of skeletal actions stored in ZSkeletalAction.
 * Provides playback control such as play, pause, and retrieving the current state
 * of the animation at a specific moment.
 */
@OptIn(ExperimentalTime::class)
@JsExport
class ZActionPlayer {
    private var currentAction: ZSkeletalAction? = null
    private var currentTime: Float = 0f
    private var isPlaying: Boolean = false
    private var playbackSpeed: Float = 1f
    private var isLooping: Boolean = false
    private var obj: ZModel? = null

    // Variables for internal time handling
    private var lastUpdateTimeMs: Long = 0L

    /**
     * Sets the action to be played
     * @param action The skeletal action to play
     */
    fun setAction(obj: ZModel, action: ZSkeletalAction) {
        this.obj = obj
        obj.action = action
        currentAction = action
        currentTime = 0f
        lastUpdateTimeMs = System.currentTimeMillis()

    }

    /**
     * Starts playing the action
     * @param loop Whether the action should loop
     */
    fun play(loop: Boolean = false) {
        if (currentAction == null) return
        if (!isPlaying) {
            // Reset reference time when starting playback
            lastUpdateTimeMs = System.currentTimeMillis()
        }
        isPlaying = true
        isLooping = loop
    }

    /**
     * Pauses playback
     */
    fun pause() {
        isPlaying = false
    }

    /**
     * Stops playback and resets the time
     */
    fun stop() {
        isPlaying = false
        currentTime = 0f
        lastUpdateTimeMs = System.currentTimeMillis()
    }

    /**
     * Updates the playback state by automatically calculating the time elapsed
     * since the last call.
     */
    fun update() {
        val currentTimeMs = System.currentTimeMillis()
        val deltaTime = (currentTimeMs - lastUpdateTimeMs) / 1000f // Convert to seconds
        lastUpdateTimeMs = currentTimeMs

        updateWithDeltaTime(deltaTime)
    }

    /**
     * Updates the playback state based on the provided elapsed time
     * @param deltaTime Time elapsed since the last update in seconds
     */
    private fun updateWithDeltaTime(deltaTime: Float) {
        if (!isPlaying || currentAction == null) return

        currentTime += deltaTime * playbackSpeed
        getCurrentKeyFrame()

        // Loop or finish control
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
     * Gets the current keyframe based on playback time
     * @return The keyframe corresponding to the current time
     */
    fun getCurrentKeyFrame(): ZKeyFrame? {
        val kf = currentAction?.getKeyFrame(currentTime)!!
        obj?.skeleton?.root?.computePoseFromKeyFrame(kf, ZMatrix4.Identity)

        return kf
    }

    /**
     * Sets the playback speed
     * @param speed Speed factor (1.0 = normal, 2.0 = double speed, etc.)
     */
    fun setPlaybackSpeed(speed: Float) {
        playbackSpeed = speed
    }

    /**
     * Jumps to a specific point in time
     * @param time Time to jump to
     */
    fun seek(time: Float) {
        if (currentAction == null) return
        currentTime = time.coerceIn(0f, currentAction!!.duration)
    }

    /**
     * @return Whether the action is currently playing
     */
    fun isPlaying(): Boolean = isPlaying

    /**
     * @return The current playback time
     */
    fun getCurrentTime(): Float = currentTime

    /**
     * @return The total duration of the current action
     */
    fun getDuration(): Float = currentAction?.duration ?: 0f

    /**
     * @return The percentage of playback completion (0.0 to 1.0)
     */
    fun getProgress(): Float {
        val duration = getDuration()
        return if (duration > 0) currentTime / duration else 0f
    }

    /**
     * Resets the internal time counter.
     * Useful when a lot of time has passed since the last update
     * and we don't want a large jump in the animation.
     */
    fun resetTimer() {
        lastUpdateTimeMs = System.currentTimeMillis()
    }
}
