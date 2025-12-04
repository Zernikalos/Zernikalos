/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

import zernikalos.context.ZContext
import zernikalos.search.treeTraverse
import kotlin.js.JsExport

/**
 * Distributes touch events to all ZObject instances in the scene that have touch listeners registered.
 *
 * This class implements [ZUserInputEventHandler] and acts as a bridge between the input system
 * and the object event system. When a touch event occurs, it traverses the scene tree and
 * notifies all objects with registered touch listeners.
 */
@JsExport
class ZTouchEventDistributor(private val context: ZContext) : ZUserInputEventHandler {

    /**
     * Called when a touch event occurs. Distributes the event to all objects in the scene
     * that have touch listeners registered.
     *
     * @param event The touch event to distribute
     */
    override fun onTouchEvent(event: ZTouchEvent) {
        val scene = context.scene ?: return

        // Traverse the scene tree and notify all objects with listeners
        for (obj in treeTraverse(scene)) {
            if (obj.hasTouchListeners) {
                obj.notifyTouchListeners(event)
            }
        }
    }
}

