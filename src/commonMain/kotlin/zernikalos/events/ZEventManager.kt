/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

class ZEventManager {

    private val nativeEventSource = ZNativeEventSource()
    private val _eventList: ArrayList<ZTouchEvent> = arrayListOf()

    init {
        nativeEventSource.listener = object : ZNativeEventSourceListener {
            override fun onEvent(event: ZTouchEvent) {
                registerEvent(event)
            }
        }
    }

    val eventList: List<ZTouchEvent>
        get() = _eventList

    fun registerEvent(event: ZTouchEvent) {
        _eventList.add(event)
    }

}