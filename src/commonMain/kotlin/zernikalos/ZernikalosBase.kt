package zernikalos

import zernikalos.context.ZContextCreator
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZSceneContext
import zernikalos.loader.loadFromProtoString
import zernikalos.objects.ZObject
import zernikalos.ui.ZSurfaceView
import zernikalos.ui.ZSurfaceViewEventHandler
import kotlin.js.JsExport

@JsExport
open class ZernikalosBase {

    lateinit var surfaceView: ZSurfaceView
    lateinit var renderingContext: ZRenderingContext
    lateinit var stateHandler: ZSceneStateHandler
    lateinit var sceneContext: ZSceneContext

    var initialized = false

    init {
        println("Starting engine")
    }

    fun initialize(view: ZSurfaceView, contextCreator: ZContextCreator, stateHandler: ZSceneStateHandler) {
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
    }

    fun load(hexString: String): ZObject {
        return loadFromProtoString(hexString)
    }
}
