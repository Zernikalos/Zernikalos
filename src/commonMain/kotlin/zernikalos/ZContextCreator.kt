package zernikalos

import zernikalos.ui.ZSurfaceView
import kotlin.js.JsExport

@JsExport
interface ZContextCreator {

    fun createSceneContext(surfaceView: ZSurfaceView): ZSceneContext

    fun createRenderingContext(surfaceView: ZSurfaceView): ZRenderingContext

}