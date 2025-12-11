/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events.keyboard

import kotlin.js.JsExport

/**
 * Maintains the current state of pressed keyboard keys.
 *
 * This class tracks which keys are currently pressed, similar to Unity's `Input.GetKey()`,
 * Unreal's `IsInputKeyDown()`, or Godot's `Input.is_action_pressed()`.
 *
 * The state is automatically updated by the event system when keyboard events are processed
 * during the game loop frame update phase. You should not manually update the state;
 * instead, use [isKeyPressed] to query the current state of keys.
 *
 * @example
 * ```kotlin
 * // Check if a key is currently pressed
 * if (context.input.keyboard.isKeyPressed(ZKeyCode.Space)) {
 *     // Space bar is being held down
 * }
 *
 * // Check multiple keys
 * if (context.input.keyboard.isKeyPressed(ZKeyCode.W) &&
 *     context.input.keyboard.isKeyPressed(ZKeyCode.LeftShift)) {
 *     // Running forward
 * }
 * ```
 */
@JsExport
class ZKeyboardState {

    private val pressedKeys = mutableSetOf<ZKeyCode>()

    /**
     * Checks if the specified key is currently pressed.
     *
     * @param keyCode The key code to check
     * @return `true` if the key is currently pressed, `false` otherwise
     */
    fun isKeyPressed(keyCode: ZKeyCode): Boolean {
        return pressedKeys.contains(keyCode)
    }

    /**
     * Marks a key as pressed. This is called automatically by the event system
     * when a KEY_DOWN event is processed. You should not call this manually.
     *
     * @param keyCode The key code to mark as pressed
     */
    internal fun setKeyPressed(keyCode: ZKeyCode) {
        pressedKeys.add(keyCode)
    }

    /**
     * Marks a key as released. This is called automatically by the event system
     * when a KEY_UP event is processed. You should not call this manually.
     *
     * @param keyCode The key code to mark as released
     */
    internal fun setKeyReleased(keyCode: ZKeyCode) {
        pressedKeys.remove(keyCode)
    }

    /**
     * Clears all pressed keys from the state.
     * This can be useful for resetting the state, for example when the window
     * loses focus or during scene transitions.
     */
    fun clear() {
        pressedKeys.clear()
    }

    /**
     * Returns the number of keys currently pressed.
     */
    val pressedKeyCount: Int
        get() = pressedKeys.size

    /**
     * Returns whether any keys are currently pressed.
     */
    val hasPressedKeys: Boolean
        get() = pressedKeys.isNotEmpty()
}
