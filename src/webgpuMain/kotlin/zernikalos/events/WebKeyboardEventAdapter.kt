/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

import org.w3c.dom.events.KeyboardEvent
import zernikalos.events.keyboard.ZKeyCode
import zernikalos.events.keyboard.ZKeyboardEvent
import zernikalos.events.keyboard.ZKeyboardEventType

/**
 * Adapter that converts DOM KeyboardEvent to ZKeyboardEvent.
 *
 * This class handles the conversion of browser keyboard events to the engine's
 * internal keyboard event format, including key information and modifier states.
 */
@JsExport
class WebKeyboardEventAdapter {

    /**
     * Converts a DOM KeyboardEvent to a ZKeyboardEvent.
     *
     * @param event The DOM KeyboardEvent to convert
     * @param type The type of keyboard event (KEY_DOWN, KEY_UP, KEY_PRESS)
     * @return A ZKeyboardEvent with key information and modifiers
     */
    fun convert(event: KeyboardEvent, type: ZKeyboardEventType): ZKeyboardEvent {
        // For KEY_PRESS events, use the character; for others, use null
        val char = if (type == ZKeyboardEventType.KEY_PRESS) {
            // Get the character from the key property if it's a single character
            val key = event.key
            if (key.length == 1) key else null
        } else {
            null
        }

        // Convert DOM code to ZKeyCode
        val keyCode = ZKeyCode.fromDomCode(event.code)

        return ZKeyboardEvent(
            key = event.key,
            code = event.code,
            keyCode = keyCode,
            char = char,
            type = type,
            timestamp = event.timeStamp.toLong(),
            ctrlKey = event.ctrlKey,
            shiftKey = event.shiftKey,
            altKey = event.altKey,
            metaKey = event.metaKey,
            repeat = event.repeat
        )
    }
}
