/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

import zernikalos.events.keyboard.ZKeyboardEvent
import zernikalos.events.keyboard.ZObjectKeyboardListener
import zernikalos.events.mouse.ZMouseEvent
import zernikalos.events.mouse.ZObjectMouseListener
import zernikalos.events.touch.ZObjectTouchListener
import zernikalos.events.touch.ZTouchEvent
import zernikalos.objects.ZObject
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Manages event listeners for a ZObject instance.
 * This class encapsulates all listener management functionality, providing a clean API
 * for adding, removing, and notifying listeners for touch, mouse, and keyboard events.
 *
 * @property owner The ZObject instance that owns this event manager
 */
@JsExport
class ZEventManager(private val owner: ZObject) {
    private val _touchListeners: ArrayList<ZObjectTouchListener> = arrayListOf()
    private val _mouseListeners: ArrayList<ZObjectMouseListener> = arrayListOf()
    private val _keyboardListeners: ArrayList<ZObjectKeyboardListener> = arrayListOf()

    /**
     * Returns whether this object has any touch listeners registered.
     */
    val hasTouchListeners: Boolean
        get() = _touchListeners.isNotEmpty()

    /**
     * Returns whether this object has any mouse listeners registered.
     */
    val hasMouseListeners: Boolean
        get() = _mouseListeners.isNotEmpty()

    /**
     * Returns whether this object has any keyboard listeners registered.
     */
    val hasKeyboardListeners: Boolean
        get() = _keyboardListeners.isNotEmpty()

    // Touch listeners

    /**
     * Adds a touch event listener to this object.
     *
     * @param listener The listener interface to add
     */
    fun addTouchListener(listener: ZObjectTouchListener) {
        if (!_touchListeners.contains(listener)) {
            _touchListeners.add(listener)
        }
    }

    /**
     * Adds a touch event listener using a lambda function.
     *
     * @param listener Lambda function that receives the object and touch event
     */
    @JsName("addTouchListenerLambda")
    fun addTouchListener(listener: (ZObject, ZTouchEvent) -> Unit) {
        val wrapper = object : ZObjectTouchListener {
            override fun onTouchEvent(obj: ZObject, event: ZTouchEvent) {
                listener(obj, event)
            }
        }
        addTouchListener(wrapper)
    }

    /**
     * Removes a touch event listener from this object.
     *
     * @param listener The listener to remove
     */
    fun removeTouchListener(listener: ZObjectTouchListener) {
        _touchListeners.remove(listener)
    }

    /**
     * Removes all touch event listeners from this object.
     */
    fun removeAllTouchListeners() {
        _touchListeners.clear()
    }

    /**
     * Notifies all registered touch listeners of a touch event.
     * This method is called internally by the event distribution system.
     *
     * @param event The touch event to notify listeners about
     */
    fun notifyTouchListeners(event: ZTouchEvent) {
        _touchListeners.forEach { listener ->
            listener.onTouchEvent(owner, event)
        }
    }

    // Mouse listeners

    /**
     * Adds a mouse event listener to this object.
     *
     * @param listener The listener interface to add
     */
    fun addMouseListener(listener: ZObjectMouseListener) {
        if (!_mouseListeners.contains(listener)) {
            _mouseListeners.add(listener)
        }
    }

    /**
     * Adds a mouse event listener using a lambda function.
     *
     * @param listener Lambda function that receives the object and mouse event
     */
    @JsName("addMouseListenerLambda")
    fun addMouseListener(listener: (ZObject, ZMouseEvent) -> Unit) {
        val wrapper = object : ZObjectMouseListener {
            override fun onMouseEvent(obj: ZObject, event: ZMouseEvent) {
                listener(obj, event)
            }
        }
        addMouseListener(wrapper)
    }

    /**
     * Removes a mouse event listener from this object.
     *
     * @param listener The listener to remove
     */
    fun removeMouseListener(listener: ZObjectMouseListener) {
        _mouseListeners.remove(listener)
    }

    /**
     * Removes all mouse event listeners from this object.
     */
    fun removeAllMouseListeners() {
        _mouseListeners.clear()
    }

    /**
     * Notifies all registered mouse listeners of a mouse event.
     * This method is called internally by the event distribution system.
     *
     * @param event The mouse event to notify listeners about
     */
    fun notifyMouseListeners(event: ZMouseEvent) {
        _mouseListeners.forEach { listener ->
            listener.onMouseEvent(owner, event)
        }
    }

    // Keyboard listeners

    /**
     * Adds a keyboard event listener to this object.
     *
     * @param listener The listener interface to add
     */
    fun addKeyboardListener(listener: ZObjectKeyboardListener) {
        if (!_keyboardListeners.contains(listener)) {
            _keyboardListeners.add(listener)
        }
    }

    /**
     * Adds a keyboard event listener using a lambda function.
     *
     * @param listener Lambda function that receives the object and keyboard event
     */
    @JsName("addKeyboardListenerLambda")
    fun addKeyboardListener(listener: (ZObject, ZKeyboardEvent) -> Unit) {
        val wrapper = object : ZObjectKeyboardListener {
            override fun onKeyboardEvent(obj: ZObject, event: ZKeyboardEvent) {
                listener(obj, event)
            }
        }
        addKeyboardListener(wrapper)
    }

    /**
     * Removes a keyboard event listener from this object.
     *
     * @param listener The listener to remove
     */
    fun removeKeyboardListener(listener: ZObjectKeyboardListener) {
        _keyboardListeners.remove(listener)
    }

    /**
     * Removes all keyboard event listeners from this object.
     */
    fun removeAllKeyboardListeners() {
        _keyboardListeners.clear()
    }

    /**
     * Notifies all registered keyboard listeners of a keyboard event.
     * This method is called internally by the event distribution system.
     *
     * @param event The keyboard event to notify listeners about
     */
    fun notifyKeyboardListeners(event: ZKeyboardEvent) {
        _keyboardListeners.forEach { listener ->
            listener.onKeyboardEvent(owner, event)
        }
    }
}
