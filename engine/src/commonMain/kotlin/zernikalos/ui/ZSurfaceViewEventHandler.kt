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
 * Handler for surface view lifecycle and rendering events.
 *
 * Implementations are typically created when a surface view is attached and receive callbacks
 * for readiness, per-frame rendering, and resize. The platform [ZSurfaceView] should call
 * [dispose] before clearing its reference to the handler so that the scene graph and renderer
 * are properly torn down.
 */
@JsExport
interface ZSurfaceViewEventHandler {

    /**
     * Called when the surface is ready for rendering (e.g. context created, first frame).
     */
    fun onReady()

    /**
     * Called each frame to perform rendering.
     */
    fun onRender()

    /**
     * Called when the surface size changes.
     *
     * @param width New width in pixels.
     * @param height New height in pixels.
     */
    fun onResize(width: Int, height: Int)

    /**
     * Disposes the handler and releases resources (scene graph, renderer).
     * Should be called by the platform SurfaceView before clearing the handler reference.
     */
    fun dispose()
}