/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events.mouse

import kotlin.js.JsExport

/**
 * Represents the type of a mouse event.
 */
@JsExport
enum class ZMouseEventType {
    /**
     * A mouse button has been pressed down.
     */
    DOWN,

    /**
     * A mouse button has been released.
     */
    UP,

    /**
     * The mouse has moved.
     */
    MOVE
}
