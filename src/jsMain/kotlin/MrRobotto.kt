import mr.robotto.MrRobottoBase
import mr.robotto.ui.MrSurfaceView
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
class MrRobotto: MrRobottoBase() {

    fun attachCanvas(canvas: HTMLCanvasElement) {
        val surfaceView = MrSurfaceView()
        surfaceView.attachCanvas(canvas)

        attachSurfaceView(surfaceView)
    }

}
