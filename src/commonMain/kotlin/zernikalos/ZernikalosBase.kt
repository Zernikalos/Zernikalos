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
    lateinit var sceneContext: ZSceneContext

    val settings: ZSettings = ZSettings.getInstance()

    init {
        println("Starting engine")
    }

    fun initialize(view: ZSurfaceView, contextCreator: ZContextCreator, stateHandler: ZSceneStateHandler) {
        logger.info("Zernikalos Engine is ready!")

        this.stateHandler = stateHandler
        this.surfaceView = view

        sceneContext = contextCreator.createSceneContext(surfaceView)
        renderingContext = contextCreator.createRenderingContext(surfaceView)

        surfaceView.eventHandler = object : ZSurfaceViewEventHandler {
            override fun onReady() {
                stateHandler.onReady(sceneContext, renderingContext)
                initialized = true
            }

            override fun onRender() {
                if (!initialized) {
                    initialized = true
                    stateHandler.onReady(sceneContext, renderingContext)
                }
                stateHandler.onRender(sceneContext, renderingContext)
            }
        }
        logger.info("View attached")
    }

    fun load(hexString: String): ZObject {
        return loadFromProtoString(hexString)
    }
}
