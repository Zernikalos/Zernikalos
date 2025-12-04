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
 * Creates and returns a [ZUserInputEventHandler] that distributes touch events
 * to all ZObject instances in the scene that have touch listeners registered.
 *
 * This is a convenience function that creates a [ZTouchEventDistributor] configured
 * with the given context. The returned handler can be assigned to the
 * [zernikalos.ui.ZSurfaceView.userInputEventHandler] property.
 *
 * @param context The ZContext containing the scene to distribute events to
 * @return A ZUserInputEventHandler that distributes touch events to objects with listeners
 *
 * @example
 * ```kotlin
 * val context = // ... get context
 * val surfaceView = // ... get surface view
 * surfaceView.userInputEventHandler = createTouchEventDistributor(context)
 * ```
 */
@JsExport
fun createTouchEventDistributor(context: ZContext): ZUserInputEventHandler {
    return ZTouchEventDistributor(context)
}

