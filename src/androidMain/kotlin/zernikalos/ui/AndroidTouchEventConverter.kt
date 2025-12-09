/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

import android.view.MotionEvent
import zernikalos.events.touch.ZTouchEvent
import zernikalos.events.touch.ZTouchEventType

/**
 * Helper class to convert Android [MotionEvent] to [ZTouchEvent].
 *
 * This class handles the conversion of native Android touch events to the
 * common touch event format, including calculations for velocity and acceleration.
 */
internal class AndroidTouchEventConverter {

    private data class PointerState(
        var x: Float = 0f,
        var y: Float = 0f,
        var prevX: Float = 0f,
        var prevY: Float = 0f,
        var velocityX: Float = 0f,
        var velocityY: Float = 0f,
        var prevVelocityX: Float = 0f,
        var prevVelocityY: Float = 0f,
        var lastTimestamp: Long = 0L
    )

    private val pointerStates = mutableMapOf<Int, PointerState>()

    /**
     * Converts an Android [MotionEvent] to a list of [ZTouchEvent] objects.
     * Supports multi-touch by processing all active pointers in the event.
     *
     * @param event The Android MotionEvent to convert
     * @return List of ZTouchEvent objects, one for each active pointer
     */
    fun convert(event: MotionEvent): List<ZTouchEvent> {
        val events = mutableListOf<ZTouchEvent>()
        val action = event.actionMasked
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        val timestamp = event.eventTime

        when (action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> {
                val state = PointerState()
                state.x = event.getX(pointerIndex)
                state.y = event.getY(pointerIndex)
                state.prevX = state.x
                state.prevY = state.y
                state.lastTimestamp = timestamp
                pointerStates[pointerId] = state

                events.add(createTouchEvent(
                    event = event,
                    pointerIndex = pointerIndex,
                    pointerId = pointerId,
                    type = ZTouchEventType.DOWN,
                    state = state,
                    timestamp = timestamp
                ))
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> {
                val state = pointerStates[pointerId]
                if (state != null) {
                    events.add(createTouchEvent(
                        event = event,
                        pointerIndex = pointerIndex,
                        pointerId = pointerId,
                        type = ZTouchEventType.UP,
                        state = state,
                        timestamp = timestamp
                    ))
                    pointerStates.remove(pointerId)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                // Process all active pointers for MOVE events
                for (i in 0 until event.pointerCount) {
                    val id = event.getPointerId(i)
                    val state = pointerStates[id]
                    if (state != null) {
                        state.prevX = state.x
                        state.prevY = state.y
                        state.x = event.getX(i)
                        state.y = event.getY(i)

                        // Calculate velocity using historical data if available
                        calculateVelocity(event, i, state, timestamp)

                        events.add(createTouchEvent(
                            event = event,
                            pointerIndex = i,
                            pointerId = id,
                            type = ZTouchEventType.MOVE,
                            state = state,
                            timestamp = timestamp
                        ))

                        state.lastTimestamp = timestamp
                        state.prevVelocityX = state.velocityX
                        state.prevVelocityY = state.velocityY
                    }
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                val state = pointerStates[pointerId]
                if (state != null) {
                    events.add(createTouchEvent(
                        event = event,
                        pointerIndex = pointerIndex,
                        pointerId = pointerId,
                        type = ZTouchEventType.CANCEL,
                        state = state,
                        timestamp = timestamp
                    ))
                    pointerStates.remove(pointerId)
                }
            }
        }

        return events
    }

    /**
     * Creates a ZTouchEvent from the current state and event data.
     */
    private fun createTouchEvent(
        event: MotionEvent,
        pointerIndex: Int,
        pointerId: Int,
        type: ZTouchEventType,
        state: PointerState,
        timestamp: Long
    ): ZTouchEvent {
        val deltaX = state.x - state.prevX
        val deltaY = state.y - state.prevY

        // Calculate acceleration from velocity change
        val deltaTime = (timestamp - state.lastTimestamp).coerceAtLeast(1L) / 1000f // Convert to seconds
        val accelerationX = if (deltaTime > 0) {
            (state.velocityX - state.prevVelocityX) / deltaTime
        } else {
            0f
        }
        val accelerationY = if (deltaTime > 0) {
            (state.velocityY - state.prevVelocityY) / deltaTime
        } else {
            0f
        }

        return ZTouchEvent(
            x = state.x,
            y = state.y,
            prevX = state.prevX,
            prevY = state.prevY,
            deltaX = deltaX,
            deltaY = deltaY,
            velocityX = state.velocityX,
            velocityY = state.velocityY,
            accelerationX = accelerationX,
            accelerationY = accelerationY,
            type = type,
            timestamp = timestamp,
            pointerId = pointerId
        )
    }

    /**
     * Calculates velocity using historical data from MotionEvent when available.
     */
    private fun calculateVelocity(
        event: MotionEvent,
        pointerIndex: Int,
        state: PointerState,
        currentTimestamp: Long
    ) {
        val historySize = event.historySize
        if (historySize > 0) {
            // Use historical data for more accurate velocity calculation
            val oldestX = event.getHistoricalX(pointerIndex, 0)
            val oldestY = event.getHistoricalY(pointerIndex, 0)
            val oldestTime = event.getHistoricalEventTime(0)

            val deltaTime = (currentTimestamp - oldestTime).coerceAtLeast(1L) / 1000f // Convert to seconds
            if (deltaTime > 0) {
                state.velocityX = (state.x - oldestX) / deltaTime
                state.velocityY = (state.y - oldestY) / deltaTime
            } else {
                state.velocityX = 0f
                state.velocityY = 0f
            }
        } else {
            // Fallback to simple velocity calculation using last position
            val deltaTime = (currentTimestamp - state.lastTimestamp).coerceAtLeast(1L) / 1000f // Convert to seconds
            if (deltaTime > 0) {
                state.velocityX = (state.x - state.prevX) / deltaTime
                state.velocityY = (state.y - state.prevY) / deltaTime
            } else {
                state.velocityX = 0f
                state.velocityY = 0f
            }
        }
    }

    /**
     * Clears all pointer states. Useful for resetting the converter.
     */
    fun clear() {
        pointerStates.clear()
    }
}

