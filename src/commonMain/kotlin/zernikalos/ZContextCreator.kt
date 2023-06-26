package zernikalos

import zernikalos.ui.ZSurfaceView
import kotlin.js.JsExport

@JsExport
interface ZContextCreator {

    fun createSceneContext(surfaceView: ZSurfaceView): ZSceneContext

    fun createRenderingContext(surfaceView: ZSurfaceView): ZRenderingContext

}

class ZDefaultContextCreator: ZContextCreator {

    override fun createSceneContext(surfaceView: ZSurfaceView): ZSceneContext {
        return createDefaultSceneContext()
    }

    override fun createRenderingContext(surfaceView: ZSurfaceView): ZRenderingContext {
        return ZRenderingContext(surfaceView)
    }

}

fun createDefaultContextCreator(): ZContextCreator {
    return ZDefaultContextCreator()
}