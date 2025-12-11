/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSSet
import platform.MetalKit.MTKView
import zernikalos.events.ZEventQueue

/**
 * Helper class to handle touch events for MTKView.
 * This class sets up touch event handling and converts them to ZTouchEvent.
 */
@OptIn(ExperimentalForeignApi::class)
internal class MetalTouchHandler(
    private val view: MTKView,
    private val touchEventConverter: MetalTouchEventConverter
) {
    var eventQueue: ZEventQueue? = null
        private set

    fun setEventQueue(queue: ZEventQueue?) {
        eventQueue = queue
        setupTouchHandling()
    }

    private fun setupTouchHandling() {
        // Enable user interaction
        view.userInteractionEnabled = true
        view.multipleTouchEnabled = true
    }

    fun handleTouchesBegan(touches: NSSet, event: platform.UIKit.UIEvent?) {
        val queue = eventQueue
        if (queue != null) {
            val touchEvents = touchEventConverter.convertAll(touches, view)
            for (touchEvent in touchEvents) {
                queue.enqueueTouch(touchEvent)
            }
        }
    }

    fun handleTouchesMoved(touches: NSSet, event: platform.UIKit.UIEvent?) {
        val queue = eventQueue
        if (queue != null) {
            val touchEvents = touchEventConverter.convertAll(touches, view)
            for (touchEvent in touchEvents) {
                queue.enqueueTouch(touchEvent)
            }
        }
    }

    fun handleTouchesEnded(touches: NSSet, event: platform.UIKit.UIEvent?) {
        val queue = eventQueue
        if (queue != null) {
            val touchEvents = touchEventConverter.convertAll(touches, view)
            for (touchEvent in touchEvents) {
                queue.enqueueTouch(touchEvent)
            }
        }
    }

    fun handleTouchesCancelled(touches: NSSet, event: platform.UIKit.UIEvent?) {
        val queue = eventQueue
        if (queue != null) {
            val touchEvents = touchEventConverter.convertAll(touches, view)
            for (touchEvent in touchEvents) {
                queue.enqueueTouch(touchEvent)
            }
        }
    }

    fun dispose() {
        touchEventConverter.clear()
        eventQueue = null
    }
}



