package mr.robotto.ui

import kotlinx.browser.window
import mr.robotto.MrRenderingContext
import org.w3c.dom.HTMLCanvasElement

@JsExport
@ExperimentalJsExport
actual class MrSurfaceView {

    lateinit var canvas: HTMLCanvasElement

    actual val width: Int
        get() = canvas.width
    actual val height: Int
        get() = canvas.height

    actual val stateHandlerBridge: MrSurfaceStateHandlerBridge = MrSurfaceStateHandlerBridge()
    actual val renderingContext: MrRenderingContext = MrRenderingContext()

    fun attachCanvas(canvas: HTMLCanvasElement) {
        this.canvas = canvas

        onReady()
    }

    private fun onReady() {
        renderingContext.setContext(canvas)

        stateHandlerBridge.onReady(renderingContext)

        renderLoop()
    }

    private fun renderLoop() {
        stateHandlerBridge.onRender(renderingContext)
        window.setTimeout({renderLoop()}, 1000/60)
    }

}