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
 * Represents a keyboard event with key information and modifiers.
 *
 * @property key The key value (e.g., "a", "Enter", "ArrowLeft")
 * @property code The physical key code string (e.g., "KeyA", "Enter", "ArrowLeft")
 * @property keyCode The ZKeyCode enum value for type-safe key checking
 * @property char The character that was typed (for KEY_PRESS events, null otherwise)
 * @property type Type of keyboard event
 * @property timestamp Event timestamp in milliseconds
 * @property ctrlKey Whether the Ctrl key was pressed
 * @property shiftKey Whether the Shift key was pressed
 * @property altKey Whether the Alt key was pressed
 * @property metaKey Whether the Meta key (Cmd on Mac, Windows key on Windows) was pressed
 * @property repeat Whether the key is being held down (repeat event)
 */
@JsExport
data class ZKeyboardEvent(
    val key: String,
    val code: String,
    val keyCode: ZKeyCode,
    val char: String?,
    val type: ZKeyboardEventType,
    val timestamp: Long,
    val ctrlKey: Boolean,
    val shiftKey: Boolean,
    val altKey: Boolean,
    val metaKey: Boolean,
    val repeat: Boolean
)
