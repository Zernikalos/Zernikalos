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
import zernikalos.settings.ZSettings
import zernikalos.stats.ZStats
import zernikalos.ui.ZSurfaceView
import kotlin.js.JsExport

@JsExport
open class ZernikalosBase: ZLoggable {

    lateinit var surfaceView: ZSurfaceView
    lateinit var stateHandler: ZSceneStateHandler

    lateinit var context: ZContext

    @Suppress("unused")
    val settings: ZSettings = ZSettings.getInstance()

    @Suppress("unused")
    val stats: ZStats = ZStats()

    fun initialize(view: ZSurfaceView, contextCreator: ZContextCreator, stateHandler: ZSceneStateHandler) {
        logger.info("Zernikalos Engine is ready!")

        this.stateHandler = stateHandler
        this.surfaceView = view

        context = contextCreator.createContext(surfaceView)

        surfaceView.eventHandler = createSurfaceViewEventHandler(context, stateHandler)
        logger.info("View attached")
    }
}
