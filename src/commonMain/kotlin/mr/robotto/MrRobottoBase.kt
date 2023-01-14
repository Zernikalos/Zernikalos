package mr.robotto

import mr.robotto.loader.loadFromCborString
import mr.robotto.objects.MrObject
import mr.robotto.ui.MrSurfaceView
import kotlin.js.JsExport

@JsExport
open class MrRobottoBase {

    lateinit var surfaceView: MrSurfaceView

    init {
        println("Starting engine")
    }

    fun attachSurfaceView(surfaceView: MrSurfaceView) {
        this.surfaceView = surfaceView
    }

    fun attachStateHandler(stateHandler: MrSceneStateHandler) {
        surfaceView.stateHandlerBridge.stateHandler = stateHandler
    }

    fun load(hexString: String): MrObject {
        return loadFromCborString(hexString)
    }
}
