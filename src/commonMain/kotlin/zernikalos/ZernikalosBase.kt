package zernikalos

import zernikalos.loader.loadFromProtoString
import zernikalos.objects.ZObject
import zernikalos.ui.ZSurfaceView
import kotlin.js.JsExport

@JsExport
open class ZernikalosBase {

    lateinit var surfaceView: ZSurfaceView

    init {
        println("Starting engine")
    }

    fun attachSurfaceView(surfaceView: ZSurfaceView) {
        this.surfaceView = surfaceView
    }

    fun attachStateHandler(stateHandler: ZSceneStateHandler) {
        surfaceView.stateHandlerBridge.stateHandler = stateHandler
    }

    fun load(hexString: String): ZObject {
        return loadFromProtoString(hexString)
    }
}
