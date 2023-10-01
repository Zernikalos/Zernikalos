package zernikalos

import zernikalos.ui.ZSurfaceView
import org.w3c.dom.HTMLCanvasElement
import zernikalos.context.createDefaultContextCreator
import kotlin.js.JsExport

@JsExport
class Zernikalos: ZernikalosBase() {

    @JsName("initializeWithCanvas")
    fun initialize(canvas: HTMLCanvasElement, stateHandler: ZSceneStateHandler) {
        val surfaceView = ZSurfaceView(canvas)
        initialize(surfaceView, stateHandler)
    }

    @JsName("initializeWithDefaults")
    fun initialize(view: ZSurfaceView, stateHandler: ZSceneStateHandler) {
        val contextCreator = createDefaultContextCreator()

        initialize(view, contextCreator, stateHandler)
    }

}
