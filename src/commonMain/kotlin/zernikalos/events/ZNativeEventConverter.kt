/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

expect class ZNativeEventSource() {

    var listener: ZNativeEventSourceListener?

}

interface ZNativeEventConverter<T> {

    fun convertEvent(event: T): ZEvent?

}

interface ZNativeEventSourceListener {

    fun onEvent(event: ZTouchEvent)

}