/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

import android.view.InputEvent
import android.view.MotionEvent
import zernikalos.math.ZVector2

actual class ZNativeEventSource actual constructor() {

    actual var listener: ZNativeEventSourceListener? = null

}

class ZAndroidEventConverter: ZNativeEventConverter<InputEvent> {

    override fun convertEvent(event: InputEvent): ZEvent? {
        return when (event) {
            is MotionEvent -> convertMotionEvent(event)
            else -> null
        }
    }

    fun convertMotionEvent(event: MotionEvent, lastTouchEvent: ZTouchEvent? = null): ZTouchEvent {
        val position = ZVector2(event.x, event.y)
        val state = when (event.action) {
            MotionEvent.ACTION_DOWN -> ZTouchState.START
            MotionEvent.ACTION_UP -> ZTouchState.END
            MotionEvent.ACTION_MOVE -> ZTouchState.MOVE
            else -> ZTouchState.NONE
        }
        var delta = ZVector2.Zero
        if (lastTouchEvent != null) {
            delta = position - lastTouchEvent.position
        }
        return ZTouchEvent(
            position = position,
            state = state,
            deltaPosition = delta
        )
    }

}