/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import zernikalos.events.keyboard.ZKeyboardEventType
import zernikalos.events.mouse.ZMouseEventType

/**
 * Manages the capture and conversion of DOM input events (mouse and keyboard)
 * for a canvas element, converting them to Zernikalos events and forwarding
 * them to a ZUserInputEventHandler.
 *
 * This class handles:
 * - Registering event listeners on the canvas
 * - Converting DOM events to Zernikalos events using adapters
 * - Forwarding events to the configured handler
 * - Cleaning up event listeners on disposal
 */
@JsExport
class WebInputEventManager(
    private val canvas: HTMLCanvasElement,
    private var handler: ZUserInputEventHandler?
) {

    private val mouseAdapter = WebMouseEventAdapter(canvas)
    private val keyboardAdapter = WebKeyboardEventAdapter()

    // Event listener functions that we'll store to remove them later
    private val mouseDownListener: (Event) -> Unit = { event ->
        event as MouseEvent
        event.preventDefault()
        // Focus canvas to receive keyboard events
        canvas.focus()
        val zEvent = mouseAdapter.convert(event, ZMouseEventType.DOWN)
        handler?.onMouseEvent(zEvent)
    }

    private val mouseUpListener: (Event) -> Unit = { event ->
        event as MouseEvent
        event.preventDefault()
        val zEvent = mouseAdapter.convert(event, ZMouseEventType.UP)
        handler?.onMouseEvent(zEvent)
    }

    private val mouseMoveListener: (Event) -> Unit = { event ->
        event as MouseEvent
        event.preventDefault()
        val zEvent = mouseAdapter.convert(event, ZMouseEventType.MOVE)
        handler?.onMouseEvent(zEvent)
    }

    private val mouseLeaveListener: (Event) -> Unit = { _ ->
        // Reset adapter state when mouse leaves canvas
        mouseAdapter.reset()
    }

    private val keyDownListener: (Event) -> Unit = { event ->
        event as KeyboardEvent
        event.preventDefault()
        val zEvent = keyboardAdapter.convert(event, ZKeyboardEventType.KEY_DOWN)
        handler?.onKeyboardEvent(zEvent)
    }

    private val keyUpListener: (Event) -> Unit = { event ->
        event as KeyboardEvent
        event.preventDefault()
        val zEvent = keyboardAdapter.convert(event, ZKeyboardEventType.KEY_UP)
        handler?.onKeyboardEvent(zEvent)
    }

    private val keyPressListener: (Event) -> Unit = { event ->
        event as KeyboardEvent
        event.preventDefault()
        val zEvent = keyboardAdapter.convert(event, ZKeyboardEventType.KEY_PRESS)
        handler?.onKeyboardEvent(zEvent)
    }

    init {
        // Register all event listeners
        canvas.addEventListener("mousedown", mouseDownListener,)
        canvas.addEventListener("mouseup", mouseUpListener)
        canvas.addEventListener("mousemove", mouseMoveListener)
        canvas.addEventListener("mouseleave", mouseLeaveListener)
        canvas.addEventListener("keydown", keyDownListener)
        canvas.addEventListener("keyup", keyUpListener)
        canvas.addEventListener("keypress", keyPressListener)

        // Make canvas focusable so it can receive keyboard events
        canvas.tabIndex = 0
        canvas.style.outline = "none"
    }

    /**
     * Gets the current event handler.
     *
     * @return The current handler, or null if no handler is set
     */
    fun getHandler(): ZUserInputEventHandler? {
        return handler
    }

    /**
     * Updates the event handler that receives converted events.
     *
     * @param newHandler The new handler to receive events, or null to disable event forwarding
     */
    fun setHandler(newHandler: ZUserInputEventHandler?) {
        handler = newHandler
    }

    /**
     * Disposes the manager and removes all event listeners.
     * Call this when the canvas is no longer needed.
     */
    fun dispose() {
        canvas.removeEventListener("mousedown", mouseDownListener)
        canvas.removeEventListener("mouseup", mouseUpListener)
        canvas.removeEventListener("mousemove", mouseMoveListener)
        canvas.removeEventListener("mouseleave", mouseLeaveListener)
        canvas.removeEventListener("keydown", keyDownListener)
        canvas.removeEventListener("keyup", keyUpListener)
        canvas.removeEventListener("keypress", keyPressListener)
    }
}
