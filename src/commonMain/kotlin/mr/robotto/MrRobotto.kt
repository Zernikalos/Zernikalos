package mr.robotto

import mr.robotto.loader.MrLoader
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
        val loader = MrLoader()
        return loader.hexCborLoad(hexString)
    }
}
