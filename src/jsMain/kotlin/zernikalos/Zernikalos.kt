package zernikalos

import zernikalos.ui.ZkSurfaceView
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.JsExport

@JsExport
class Zernikalos: ZernikalosBase() {

    fun attachCanvas(canvas: HTMLCanvasElement) {
        val surfaceView = ZkSurfaceView()
        surfaceView.attachCanvas(canvas)

        attachSurfaceView(surfaceView)
    }

}
