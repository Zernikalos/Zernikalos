/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

import zernikalos.events.keyboard.ZKeyboardEvent
import zernikalos.events.mouse.ZMouseEvent
import zernikalos.events.touch.ZTouchEvent
import kotlin.js.JsExport

/**
 * Handler interface for user input events, including touch, mouse, and keyboard events.
 *
 * This interface is separate from [zernikalos.ui.ZSurfaceViewEventHandler] to maintain
 * separation of concerns between surface lifecycle events and user input events.
 */
@JsExport
interface ZUserInputEventHandler {

    /**
     * Called when a touch event occurs.
     *
     * @param event The touch event containing position, movement, velocity, and acceleration data
     */
    fun onTouchEvent(event: ZTouchEvent)

    /**
     * Called when a mouse event occurs.
     *
     * @param event The mouse event containing position, movement, velocity, and acceleration data
     */
    fun onMouseEvent(event: ZMouseEvent)

    /**
     * Called when a keyboard event occurs.
     *
     * @param event The keyboard event containing key information and modifiers
     */
    fun onKeyboardEvent(event: ZKeyboardEvent)
}

