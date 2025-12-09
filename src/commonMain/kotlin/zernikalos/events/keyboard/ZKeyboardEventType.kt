/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events.keyboard

import kotlin.js.JsExport

/**
 * Represents the type of a keyboard event.
 */
@JsExport
enum class ZKeyboardEventType {
    /**
     * A key has been pressed down.
     */
    KEY_DOWN,

    /**
     * A key has been released.
     */
    KEY_UP,

    /**
     * A character has been typed (key press with character).
     */
    KEY_PRESS
}
