/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

import kotlin.js.JsExport

/**
 * Represents the type of a touch event.
 */
@JsExport
enum class ZTouchEventType {
    /**
     * A touch point has been placed down on the screen.
     */
    DOWN,

    /**
     * A touch point has been lifted from the screen.
     */
    UP,

    /**
     * A touch point has moved across the screen.
     */
    MOVE,

    /**
     * A touch event has been canceled.
     */
    CANCEL
}

