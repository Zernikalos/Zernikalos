/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UITouch
import platform.UIKit.UITouchPhase
import zernikalos.events.touch.ZTouchEvent
import zernikalos.events.touch.ZTouchEventType

/**
 * Helper class to convert iOS [UITouch] events to [ZTouchEvent].
 *
 * This class handles the conversion of native iOS touch events to the
 * common touch event format, including calculations for velocity and acceleration.
 */
@OptIn(ExperimentalForeignApi::class)
internal class MetalTouchEventConverter {

    private data class PointerState(
        var x: Float = 0f,
        var y: Float = 0f,
        var prevX: Float = 0f,
        var prevY: Float = 0f,
        var velocityX: Float = 0f,
        var velocityY: Float = 0f,
        var prevVelocityX: Float = 0f,
        var prevVelocityY: Float = 0f,
        var lastTimestamp: Double = 0.0
    )

    private val pointerStates = mutableMapOf<Long, PointerState>()

    /**
     * Converts an iOS [UITouch] to a [ZTouchEvent] object.
     * Supports multi-touch by tracking each touch point separately.
     *
     * @param touch The iOS UITouch to convert
     * @param view The view that contains the touch (for coordinate conversion)
     * @return ZTouchEvent object or null if the touch phase is not supported
     */
    fun convert(touch: UITouch, view: platform.UIKit.UIView): ZTouchEvent? {
        val pointerId = touch.hashCode().toLong()
        val phase = touch.phase
        val location = touch.locationInView(view)
        val timestamp = touch.timestamp

        location.useContents {
            val x = this.x.toFloat()
            val y = this.y.toFloat()

            return when (phase) {
                UITouchPhase.UITouchPhaseBegan -> {
                    val state = PointerState()
                    state.x = x
                    state.y = y
                    state.prevX = x
                    state.prevY = y
                    state.lastTimestamp = timestamp
                    pointerStates[pointerId] = state

                    createTouchEvent(
                        x = x,
                        y = y,
                        prevX = state.prevX,
                        prevY = state.prevY,
                        type = ZTouchEventType.DOWN,
                        state = state,
                        timestamp = timestamp,
                        pointerId = pointerId.toInt()
                    )
                }

                UITouchPhase.UITouchPhaseEnded -> {
                    val state = pointerStates[pointerId]
                    if (state != null) {
                        // Update position before creating event
                        state.prevX = state.x
                        state.prevY = state.y
                        state.x = x
                        state.y = y
                        calculateVelocity(state, timestamp)

                        val event = createTouchEvent(
                            x = x,
                            y = y,
                            prevX = state.prevX,
                            prevY = state.prevY,
                            type = ZTouchEventType.UP,
                            state = state,
                            timestamp = timestamp,
                            pointerId = pointerId.toInt()
                        )
                        pointerStates.remove(pointerId)
                        event
                    } else {
                        null
                    }
                }

                UITouchPhase.UITouchPhaseMoved -> {
                    val state = pointerStates[pointerId]
                    if (state != null) {
                        state.prevX = state.x
                        state.prevY = state.y
                        state.x = x
                        state.y = y
                        calculateVelocity(state, timestamp)

                        val event = createTouchEvent(
                            x = x,
                            y = y,
                            prevX = state.prevX,
                            prevY = state.prevY,
                            type = ZTouchEventType.MOVE,
                            state = state,
                            timestamp = timestamp,
                            pointerId = pointerId.toInt()
                        )
                        state.lastTimestamp = timestamp
                        state.prevVelocityX = state.velocityX
                        state.prevVelocityY = state.velocityY
                        event
                    } else {
                        null
                    }
                }

                UITouchPhase.UITouchPhaseCancelled -> {
                    val state = pointerStates[pointerId]
                    if (state != null) {
                        state.prevX = state.x
                        state.prevY = state.y
                        state.x = x
                        state.y = y
                        calculateVelocity(state, timestamp)

                        val event = createTouchEvent(
                            x = x,
                            y = y,
                            prevX = state.prevX,
                            prevY = state.prevY,
                            type = ZTouchEventType.CANCEL,
                            state = state,
                            timestamp = timestamp,
                            pointerId = pointerId.toInt()
                        )
                        pointerStates.remove(pointerId)
                        event
                    } else {
                        null
                    }
                }

                else -> null
            }
        }
    }

    /**
     * Converts multiple UITouch events from a set (e.g., from touchesBegan, touchesMoved, etc.)
     * to a list of ZTouchEvent objects.
     *
     * @param touches Set of UITouch objects
     * @param view The view that contains the touches
     * @return List of ZTouchEvent objects
     */
    fun convertAll(touches: platform.Foundation.NSSet, view: platform.UIKit.UIView): List<ZTouchEvent> {
        val events = mutableListOf<ZTouchEvent>()
        val allObjects = touches.allObjects
        if (allObjects != null) {
            for (i in 0 until allObjects.count.toInt()) {
                val touch = allObjects.objectAtIndex(i.toULong()) as? UITouch
                if (touch != null) {
                    val event = convert(touch, view)
                    if (event != null) {
                        events.add(event)
                    }
                }
            }
        }
        return events
    }

    /**
     * Creates a ZTouchEvent from the current state and event data.
     */
    private fun createTouchEvent(
        x: Float,
        y: Float,
        prevX: Float,
        prevY: Float,
        type: ZTouchEventType,
        state: PointerState,
        timestamp: Double,
        pointerId: Int
    ): ZTouchEvent {
        val deltaX = x - prevX
        val deltaY = y - prevY

        // Calculate acceleration from velocity change
        val deltaTime = (timestamp - state.lastTimestamp).coerceAtLeast(0.001) // Convert to seconds, minimum 1ms
        val accelerationX = if (deltaTime > 0) {
            (state.velocityX - state.prevVelocityX) / deltaTime.toFloat()
        } else {
            0f
        }
        val accelerationY = if (deltaTime > 0) {
            (state.velocityY - state.prevVelocityY) / deltaTime.toFloat()
        } else {
            0f
        }

        // Convert timestamp from seconds (NSTimeInterval) to milliseconds
        val timestampMs = (timestamp * 1000.0).toLong()

        return ZTouchEvent(
            x = x,
            y = y,
            prevX = prevX,
            prevY = prevY,
            deltaX = deltaX,
            deltaY = deltaY,
            velocityX = state.velocityX,
            velocityY = state.velocityY,
            accelerationX = accelerationX,
            accelerationY = accelerationY,
            type = type,
            timestamp = timestampMs,
            pointerId = pointerId
        )
    }

    /**
     * Calculates velocity based on position change and time delta.
     */
    private fun calculateVelocity(
        state: PointerState,
        currentTimestamp: Double
    ) {
        val deltaTime = (currentTimestamp - state.lastTimestamp).coerceAtLeast(0.001) // Convert to seconds, minimum 1ms
        if (deltaTime > 0) {
            state.velocityX = (state.x - state.prevX) / deltaTime.toFloat()
            state.velocityY = (state.y - state.prevY) / deltaTime.toFloat()
        } else {
            state.velocityX = 0f
            state.velocityY = 0f
        }
    }

    /**
     * Clears all pointer states. Useful for resetting the converter.
     */
    fun clear() {
        pointerStates.clear()
    }
}

