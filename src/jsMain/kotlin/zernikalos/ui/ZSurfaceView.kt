package zernikalos.ui

import kotlinx.browser.window
import zernikalos.ZRenderingContext
import org.w3c.dom.HTMLCanvasElement

@JsExport
@ExperimentalJsExport
actual class ZSurfaceView(val canvas: HTMLCanvasElement) {

    actual var eventHandler: ZSurfaceViewEventHandler? = null

    actual val width: Int
        get() = canvas.width
    actual val height: Int
        get() = canvas.height

    init {
        onReady()
    }

    private fun onReady() {
        eventHandler?.onReady()
        renderLoop()
    }

    private fun renderLoop() {
        eventHandler?.onRender()
        window.setTimeout({renderLoop()}, 1000/60)
    }

}