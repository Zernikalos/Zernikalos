/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events.touch

import zernikalos.objects.ZObject
import kotlin.js.JsExport

/**
 * Interface for handling touch events on ZObject instances.
 *
 * Implement this interface to receive touch events for a specific ZObject.
 * The listener will be called whenever a touch event occurs and the object
 * has this listener registered.
 */
@JsExport
interface ZObjectTouchListener {

    /**
     * Called when a touch event occurs for the associated ZObject.
     *
     * @param obj The ZObject that this listener is attached to
     * @param event The touch event containing position, movement, velocity, and acceleration data
     */
    fun onTouchEvent(obj: ZObject, event: ZTouchEvent)
}

