package zernikalos

import zernikalos.loader.loadFromProtoString
import zernikalos.objects.ZObject
import zernikalos.ui.ZSurfaceView
import zernikalos.ui.ZSurfaceViewEventHandler
import kotlin.js.JsExport
import kotlin.js.JsName

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

    @JsName("initializeWithDefaults")
    fun initialize(view: ZSurfaceView, stateHandler: ZSceneStateHandler) {
        val contextCreator = createDefaultContextCreator()

        initialize(view, contextCreator, stateHandler)
    }

    fun load(hexString: String): ZObject {
        return loadFromProtoString(hexString)
    }
}
