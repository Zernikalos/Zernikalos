/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.MouseEvent
import zernikalos.events.mouse.ZMouseEvent
import zernikalos.events.mouse.ZMouseEventType

/**
 * Adapter that converts DOM MouseEvent to ZMouseEvent.
 *
 * This class handles the conversion of browser mouse events to the engine's
 * internal mouse event format, including position calculation relative to the canvas
 * and tracking of movement, velocity, and acceleration.
 */
@JsExport
class WebMouseEventAdapter(private val canvas: HTMLCanvasElement) {

    private var prevX: Float = 0f
    private var prevY: Float = 0f
    private var prevTimestamp: Long = 0L
    private var prevVelocityX: Float = 0f
    private var prevVelocityY: Float = 0f

    /**
     * Converts a DOM MouseEvent to a ZMouseEvent.
     *
     * @param event The DOM MouseEvent to convert
     * @param type The type of mouse event (DOWN, UP, MOVE)
     * @return A ZMouseEvent with calculated position, movement, velocity, and acceleration
     */
    fun convert(event: MouseEvent, type: ZMouseEventType): ZMouseEvent {
        val rect = canvas.getBoundingClientRect()
        val dpr = kotlinx.browser.window.devicePixelRatio

        // Calculate position relative to canvas, accounting for device pixel ratio
        val x = ((event.clientX - rect.left) * dpr).toFloat()
        val y = ((event.clientY - rect.top) * dpr).toFloat()

        val timestamp = event.timeStamp.toLong()
        val deltaTime = if (prevTimestamp > 0) (timestamp - prevTimestamp) / 1000.0f else 0f

        // Calculate deltas
        val deltaX = if (prevTimestamp > 0) x - prevX else 0f
        val deltaY = if (prevTimestamp > 0) y - prevY else 0f

        // Calculate velocity (pixels per second)
        val velocityX = if (deltaTime > 0) deltaX / deltaTime else 0f
        val velocityY = if (deltaTime > 0) deltaY / deltaTime else 0f

        // Calculate acceleration (change in velocity per second)
        val accelerationX = if (deltaTime > 0 && prevTimestamp > 0) {
            (velocityX - prevVelocityX) / deltaTime
        } else {
            0f
        }
        val accelerationY = if (deltaTime > 0 && prevTimestamp > 0) {
            (velocityY - prevVelocityY) / deltaTime
        } else {
            0f
        }

        // Update previous values for next event
        val currentPrevX = prevX
        val currentPrevY = prevY
        prevX = x
        prevY = y
        prevTimestamp = timestamp
        prevVelocityX = velocityX
        prevVelocityY = velocityY

        return ZMouseEvent(
            x = x,
            y = y,
            prevX = currentPrevX,
            prevY = currentPrevY,
            deltaX = deltaX,
            deltaY = deltaY,
            velocityX = velocityX,
            velocityY = velocityY,
            accelerationX = accelerationX,
            accelerationY = accelerationY,
            type = type,
            timestamp = timestamp,
            button = event.button.toInt(),
            buttons = event.buttons.toInt()
        )
    }

    /**
     * Resets the adapter state. Call this when the mouse leaves the canvas
     * or when you want to reset velocity/acceleration calculations.
     */
    fun reset() {
        prevX = 0f
        prevY = 0f
        prevTimestamp = 0L
        prevVelocityX = 0f
        prevVelocityY = 0f
    }
}
