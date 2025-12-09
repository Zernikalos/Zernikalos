/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

import zernikalos.context.ZContext
import zernikalos.events.keyboard.ZKeyboardEvent
import zernikalos.events.mouse.ZMouseEvent
import zernikalos.events.touch.ZTouchEvent
import zernikalos.search.treeTraverse
import kotlin.js.JsExport

/**
 * Central event bus that distributes all types of user input events (touch, mouse, keyboard)
 * to all ZObject instances in the scene that have the corresponding listeners registered.
 *
 * This class implements [ZUserInputEventHandler] and acts as a bridge between the input system
 * and the object event system. When any input event occurs, it traverses the scene tree and
 * notifies all objects with registered listeners for that event type.
 *
 * @param context The ZContext containing the scene to distribute events to
 */
@JsExport
class ZEventBus(private val context: ZContext) : ZUserInputEventHandler {

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

    /**
     * Called when a mouse event occurs. Distributes the event to all objects in the scene
     * that have mouse listeners registered.
     *
     * @param event The mouse event to distribute
     */
    override fun onMouseEvent(event: ZMouseEvent) {
        val scene = context.scene ?: return

        // Traverse the scene tree and notify all objects with listeners
        for (obj in treeTraverse(scene)) {
            if (obj.hasMouseListeners) {
                obj.notifyMouseListeners(event)
            }
        }
    }

    /**
     * Called when a keyboard event occurs. Distributes the event to all objects in the scene
     * that have keyboard listeners registered.
     *
     * @param event The keyboard event to distribute
     */
    override fun onKeyboardEvent(event: ZKeyboardEvent) {
        val scene = context.scene ?: return

        // Traverse the scene tree and notify all objects with listeners
        for (obj in treeTraverse(scene)) {
            if (obj.hasKeyboardListeners) {
                obj.notifyKeyboardListeners(event)
            }
        }
    }
}
