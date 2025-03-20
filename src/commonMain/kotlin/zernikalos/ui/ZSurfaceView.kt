/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

import kotlin.js.JsExport

/**
 * Represents a surface view that is used for rendering and handling events.
 *
 * A ZSurfaceView provides functionalities for managing dimensions of the surface
 * and handling events via an event handler. It acts as an interface for rendering
 * and handling lifecycle or interaction-related updates.
 */
@JsExport
interface ZSurfaceView {

    /**
     * Represents the width of the rendering surface in logical pixels.
     */
    val surfaceWidth: Int
    /**
     * Represents the height of the rendering surface in logical pixels.
     */
    val surfaceHeight: Int

    /**
     * Represents an event handler that listens to surface-related events.
     * Such as lifecycle and rendering updates, through [ZSurfaceViewEventHandler].
     */
    var eventHandler: ZSurfaceViewEventHandler?
}