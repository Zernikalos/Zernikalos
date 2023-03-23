package zernikalos

import zernikalos.loader.loadFromCborString
import zernikalos.objects.ZkObject
import zernikalos.ui.ZkSurfaceView
import kotlin.js.JsExport

@JsExport
open class ZernikalosBase {

    lateinit var surfaceView: ZkSurfaceView

    init {
        println("Starting engine")
    }

    fun attachSurfaceView(surfaceView: ZkSurfaceView) {
        this.surfaceView = surfaceView
    }

    fun attachStateHandler(stateHandler: ZkSceneStateHandler) {
        surfaceView.stateHandlerBridge.stateHandler = stateHandler
    }

    fun load(hexString: String): ZkObject {
        return loadFromCborString(hexString)
    }
}
