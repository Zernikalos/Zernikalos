/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

import zernikalos.context.ZContext
import kotlin.js.JsExport

/**
 * Creates and returns a [ZUserInputEventHandler] that distributes all user input events
 * (touch, mouse, keyboard) to all ZObject instances in the scene that have the corresponding
 * listeners registered.
 *
 * This is a convenience function that creates a [ZEventBus] configured with the given context.
 * The returned handler can be assigned to the [zernikalos.ui.ZSurfaceView.userInputEventHandler]
 * property and will handle all types of input events.
 *
 * @param context The ZContext containing the scene to distribute events to
 * @return A ZUserInputEventHandler that distributes all input events to objects with listeners
 *
 * @example
 * ```kotlin
 * val context = // ... get context
 * val surfaceView = // ... get surface view
 * surfaceView.userInputEventHandler = createEventBus(context)
 * ```
 */
@JsExport
fun createEventBus(context: ZContext): ZUserInputEventHandler {
    return ZEventBus(context)
}

