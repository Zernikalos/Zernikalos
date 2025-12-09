/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events.keyboard

import zernikalos.objects.ZObject
import kotlin.js.JsExport

/**
 * Interface for handling keyboard events on ZObject instances.
 *
 * Implement this interface to receive keyboard events for a specific ZObject.
 * The listener will be called whenever a keyboard event occurs and the object
 * has this listener registered.
 */
@JsExport
interface ZObjectKeyboardListener {

    /**
     * Called when a keyboard event occurs for the associated ZObject.
     *
     * @param obj The ZObject that this listener is attached to
     * @param event The keyboard event containing key information and modifiers
     */
    fun onKeyboardEvent(obj: ZObject, event: ZKeyboardEvent)
}
