/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos

import zernikalos.context.ZContext
import zernikalos.context.ZContextCreator
import zernikalos.logger.ZLoggable
import zernikalos.logger.logger
import zernikalos.scenestatehandler.ZSceneStateHandler
import zernikalos.settings.ZSettings
import zernikalos.statehandler.createSurfaceViewEventHandler
import zernikalos.stats.ZStats
import zernikalos.ui.ZSurfaceView
import kotlin.js.JsExport

/**
 * Base class for the Zernikalos Engine, providing core functionality for managing surface views,
 * rendering contexts, scene contexts, state handlers, and logging. This class initializes the engine
 * and coordinates the interaction between various components, such as the scene lifecycle, event
 * management, and rendering operations.
 *
 * This class is designed to serve as the foundation for creating and running a rendering engine that
 * supports asynchronous state handling and customizable contexts.
 */
@JsExport
open class ZernikalosBase: ZLoggable {

    internal lateinit var surfaceView: ZSurfaceView
    private lateinit var stateHandler: ZSceneStateHandler

    /**
     * Represents the primary context of the Zernikalos engine during its lifecycle.
     */
    lateinit var context: ZContext

    /**
     * Represents the settings for the Zernikalos engine.
     * It provides centralized access to control logging levels and
     * other related settings.
     */
    @Suppress("unused")
    val settings: ZSettings = ZSettings.getInstance()

    /**
     * Provides platform-specific information and version details for the Zernikalos engine.
     *
     * The `stats` property contains an instance of [ZStats], which holds data
     * such as platform information and engine version.
     *
     * The property is initialized during the engine setup and serves as a utility to
     * access runtime statistics.
     */
    @Suppress("unused")
    val stats: ZStats = ZStats()

    /**
     * Disposes the Zernikalos engine and releases all associated resources.
     */
    fun dispose() {
        surfaceView.dispose()
    }

    /**
     * Initializes the Zernikalos engine by setting up the surface view, creating the rendering and scene contexts,
     * and attaching the state handler.
     *
     * @param view The surface view to be used by the engine, specifying the rendering area and associated events.
     * @param contextCreator A factory responsible for creating the rendering and scene contexts associated with the surface view.
     * @param stateHandler An asynchronous state handler that manages the lifecycle events of the scene, such as initialization, resizing, and rendering.
     */
    protected fun internalInitialize(view: ZSurfaceView, contextCreator: ZContextCreator, stateHandler: ZSceneStateHandler) {
        logger.info("Zernikalos Engine is ready!")
        logger.info(stats.toJson())

        this.stateHandler = stateHandler
        this.surfaceView = view

        context = contextCreator.createContext(surfaceView)

        surfaceView.eventHandler = createSurfaceViewEventHandler(context, stateHandler)
        surfaceView.eventQueue = context.eventQueue
        logger.info("View attached")
    }

}
