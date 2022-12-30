package mr.robotto

import mr.robotto.loader.loadFromCborString
import mr.robotto.objects.MrObject
import mr.robotto.ui.MrSurfaceView
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
class MrRobotto() {
    init {
        println("Starting engine")
    }

    fun startEngine(surfaceView: MrSurfaceView) {
    }

    fun load(hexString: String): MrObject {
        return loadFromCborString(hexString)
    }
}
