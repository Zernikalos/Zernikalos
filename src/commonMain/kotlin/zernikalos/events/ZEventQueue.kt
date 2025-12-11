/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

import zernikalos.context.ZContext
import zernikalos.events.keyboard.ZKeyboardEvent
import zernikalos.events.keyboard.ZKeyboardEventType
import zernikalos.events.mouse.ZMouseEvent
import zernikalos.events.touch.ZTouchEvent
import zernikalos.search.treeTraverse
import kotlin.js.JsExport

/**
 * Event queue that accumulates user input events and processes them synchronously
 * during the game loop frame update phase.
 *
 * This class replaces [ZEventBus] by providing both event queuing and distribution
 * functionality. Events are enqueued asynchronously when they arrive from the input
 * system, and then processed synchronously during [processAll()] which is called
 * from the renderer's update phase.
 *
 * This ensures that all events are processed at the correct time in the frame,
 * synchronized with the game loop, similar to how Unity, Unreal, and Godot handle
 * input events.
 *
 * @param context The ZContext containing the scene to distribute events to
 */
@JsExport
class ZEventQueue(private val context: ZContext) : ZUserInputEventHandler {

    private val touchEvents = mutableListOf<ZTouchEvent>()
    private val mouseEvents = mutableListOf<ZMouseEvent>()
    private val keyboardEvents = mutableListOf<ZKeyboardEvent>()

    /**
     * Enqueues a touch event to be processed during the next frame update.
     *
     * @param event The touch event to enqueue
     */
    fun enqueueTouch(event: ZTouchEvent) {
        touchEvents.add(event)
    }

    /**
     * Enqueues a mouse event to be processed during the next frame update.
     *
     * @param event The mouse event to enqueue
     */
    fun enqueueMouse(event: ZMouseEvent) {
        mouseEvents.add(event)
    }

    /**
     * Enqueues a keyboard event to be processed during the next frame update.
     *
     * @param event The keyboard event to enqueue
     */
    fun enqueueKeyboard(event: ZKeyboardEvent) {
        keyboardEvents.add(event)
    }

    /**
     * Processes all enqueued events by distributing them to objects in the scene
     * that have registered listeners. This method should be called from the renderer's
     * update phase to ensure events are processed synchronously with the game loop.
     *
     * The keyboard state is updated first (before distributing events), so that
     * listeners and other code can query the current key state during event processing.
     *
     * After processing, all event queues are cleared.
     */
    fun processAll() {
        val scene = context.scene ?: return

        // Update keyboard state first (before distributing events)
        // This ensures the state is available when listeners are called
        keyboardEvents.forEach { event ->
            when (event.type) {
                ZKeyboardEventType.KEY_DOWN -> {
                    context.input.keyboard.setKeyPressed(event.keyCode)
                }
                ZKeyboardEventType.KEY_UP -> {
                    context.input.keyboard.setKeyReleased(event.keyCode)
                }
                ZKeyboardEventType.KEY_PRESS -> {
                    // KEY_PRESS is just a repeat event, doesn't change the state
                    // The key is already marked as pressed from the initial KEY_DOWN
                }
            }
        }

        // Process touch events
        touchEvents.forEach { event ->
            for (obj in treeTraverse(scene)) {
                if (obj.events.hasTouchListeners) {
                    obj.events.notifyTouchListeners(event)
                }
            }
        }

        // Process mouse events
        mouseEvents.forEach { event ->
            for (obj in treeTraverse(scene)) {
                if (obj.events.hasMouseListeners) {
                    obj.events.notifyMouseListeners(event)
                }
            }
        }

        // Process keyboard events (state already updated above)
        keyboardEvents.forEach { event ->
            for (obj in treeTraverse(scene)) {
                if (obj.events.hasKeyboardListeners) {
                    obj.events.notifyKeyboardListeners(event)
                }
            }
        }

        // Clear all queues after processing
        touchEvents.clear()
        mouseEvents.clear()
        keyboardEvents.clear()
    }

    /**
     * Returns whether there are any events pending in the queue.
     */
    val isEmpty: Boolean
        get() = touchEvents.isEmpty() && mouseEvents.isEmpty() && keyboardEvents.isEmpty()

    // ZUserInputEventHandler implementation for compatibility
    // These methods enqueue events instead of processing them immediately

    /**
     * Called when a touch event occurs. Enqueues the event for processing during the next frame update.
     *
     * @param event The touch event to enqueue
     */
    override fun onTouchEvent(event: ZTouchEvent) {
        enqueueTouch(event)
    }

    /**
     * Called when a mouse event occurs. Enqueues the event for processing during the next frame update.
     *
     * @param event The mouse event to enqueue
     */
    override fun onMouseEvent(event: ZMouseEvent) {
        enqueueMouse(event)
    }

    /**
     * Called when a keyboard event occurs. Enqueues the event for processing during the next frame update.
     *
     * @param event The keyboard event to enqueue
     */
    override fun onKeyboardEvent(event: ZKeyboardEvent) {
        enqueueKeyboard(event)
    }
}
