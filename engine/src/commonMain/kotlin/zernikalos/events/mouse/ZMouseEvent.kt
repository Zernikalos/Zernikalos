/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events.mouse

import kotlin.js.JsExport

/**
 * Represents a mouse event with position, movement, velocity, and acceleration data.
 *
 * @property x Current X coordinate in pixels
 * @property y Current Y coordinate in pixels
 * @property prevX Previous X coordinate in pixels
 * @property prevY Previous Y coordinate in pixels
 * @property deltaX Change in X coordinate since last event
 * @property deltaY Change in Y coordinate since last event
 * @property velocityX Velocity in X direction (pixels per second)
 * @property velocityY Velocity in Y direction (pixels per second)
 * @property accelerationX Acceleration in X direction (pixels per second squared)
 * @property accelerationY Acceleration in Y direction (pixels per second squared)
 * @property type Type of mouse event
 * @property timestamp Event timestamp in milliseconds
 * @property button The mouse button that was pressed (0 = left, 1 = middle, 2 = right)
 * @property buttons Bitmask of all currently pressed mouse buttons
 */
@JsExport
data class ZMouseEvent(
    val x: Float,
    val y: Float,
    val prevX: Float,
    val prevY: Float,
    val deltaX: Float,
    val deltaY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val accelerationX: Float,
    val accelerationY: Float,
    val type: ZMouseEventType,
    val timestamp: Long,
    val button: Int,
    val buttons: Int
)
