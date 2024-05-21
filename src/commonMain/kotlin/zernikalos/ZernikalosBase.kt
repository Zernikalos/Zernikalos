/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos

import zernikalos.context.ZContextCreator
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZSceneContext
import zernikalos.loader.loadFromProtoString
import zernikalos.logger.ZLoggable
import zernikalos.logger.logger
import zernikalos.objects.ZObject
import zernikalos.settings.ZSettings
import zernikalos.ui.ZSurfaceView
import zernikalos.ui.ZSurfaceViewEventHandler
import kotlin.js.JsExport

@JsExport
open class ZernikalosBase: ZLoggable {

    lateinit var surfaceView: ZSurfaceView
    lateinit var renderingContext: ZRenderingContext
    lateinit var stateHandler: ZSceneStateHandler
    var sceneContext: ZSceneContext? = null

    val settings: ZSettings = ZSettings.getInstance()

    var isInitialized: Boolean = false

    fun initialize(view: ZSurfaceView, contextCreator: ZContextCreator, stateHandler: ZSceneStateHandler) {
        logger.info("Zernikalos Engine is ready!")

        this.stateHandler = stateHandler
        this.surfaceView = view

        sceneContext = contextCreator.createSceneContext(surfaceView)
        renderingContext = contextCreator.createRenderingContext(surfaceView)

        surfaceView.eventHandler = object: ZSurfaceViewEventHandler {
            override fun onReady() {
                stateHandler.onReady(sceneContext!!, renderingContext)
                sceneContext?.scene?.initialize(sceneContext!!, renderingContext)
                isInitialized = true
            }

            override fun onRender() {
                if (!isInitialized) {
                    isInitialized = true
                    stateHandler.onReady(sceneContext!!, renderingContext)
                }
                if (isInitialized && !sceneContext?.isInitialized!!) {
                    sceneContext?.scene?.initialize(sceneContext!!, renderingContext)
                }
                if (isInitialized && sceneContext?.isInitialized!!) {
                    stateHandler.onRender(sceneContext!!, renderingContext)
                    sceneContext?.scene?.render(sceneContext!!, renderingContext)
                }
            }
        }
        logger.info("View attached")
    }

    fun load(hexString: String): ZObject {
        return loadFromProtoString(hexString)
    }
}
