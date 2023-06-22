package zernikalos

import zernikalos.ui.ZSurfaceView
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.JsExport

@JsExport
class Zernikalos: ZernikalosBase() {

    @JsName("initializeWithCanvas")
    fun initialize(canvas: HTMLCanvasElement, stateHandler: ZSceneStateHandler) {
        val surfaceView = ZSurfaceView(canvas)
        initialize(surfaceView, stateHandler)
    }

}
