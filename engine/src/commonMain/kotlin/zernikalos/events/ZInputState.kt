/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

import zernikalos.events.keyboard.ZKeyboardState
import kotlin.js.JsExport

/**
 * Container for all input state information, including keyboard, mouse, and touch states.
 *
 * This class provides a centralized location for querying the current state of all input devices.
 * The state is automatically updated by the event system when input events are processed
 * during the game loop frame update phase.
 *
 * @example
 * ```kotlin
 * // Check keyboard state
 * if (context.input.keyboard.isKeyPressed(ZKeyCode.Space)) {
 *     // Space bar is being held down
 * }
 *
 * // Future: mouse and touch states will be added here
 * // context.input.mouse.isButtonPressed(...)
 * // context.input.touch.isTouching(...)
 * ```
 */
@JsExport
class ZInputState {

    /**
     * Keyboard state that tracks which keys are currently pressed.
     * The state is automatically updated by the event system when keyboard events
     * are processed during the game loop frame update phase.
     */
    val keyboard: ZKeyboardState = ZKeyboardState()

    // Future: mouse and touch states will be added here
    // val mouse: ZMouseState = ZMouseState()
    // val touch: ZTouchState = ZTouchState()
}
