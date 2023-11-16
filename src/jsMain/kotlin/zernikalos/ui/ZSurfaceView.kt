package zernikalos.ui

import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement

@JsExport
@ExperimentalJsExport
actual class ZSurfaceView(val canvas: HTMLCanvasElement) {

    var _eventHandler: ZSurfaceViewEventHandler? = null
    actual var eventHandler: ZSurfaceViewEventHandler?
        get() = _eventHandler
        set(value) {
            _eventHandler = value
            onReady()
        }

    actual val width: Int
        get() = canvas.width
    actual val height: Int
        get() = canvas.height

    private fun onReady() {
        eventHandler?.onReady()
        renderLoop()
    }

    private fun renderLoop() {
        window.setInterval({eventHandler?.onRender()}, 1000/60)
    }

}