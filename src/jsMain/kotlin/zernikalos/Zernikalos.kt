package zernikalos

import zernikalos.ui.ZSurfaceView
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.JsExport

@JsExport
class Zernikalos: ZernikalosBase() {

    fun attachCanvas(canvas: HTMLCanvasElement) {
        val surfaceView = ZSurfaceView()
        surfaceView.attachCanvas(canvas)

        attachSurfaceView(surfaceView)
    }

}
