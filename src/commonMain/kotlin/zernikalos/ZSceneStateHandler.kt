package zernikalos

import zernikalos.context.ZRenderingContext
import zernikalos.context.ZSceneContext
import kotlin.js.JsExport

@JsExport
interface ZSceneStateHandler {

    fun onReady(sceneContext: ZSceneContext, renderingContext: ZRenderingContext)

    fun onResize(width: Int, height: Int)

    fun onRender(sceneContext: ZSceneContext, renderingContext: ZRenderingContext)
}