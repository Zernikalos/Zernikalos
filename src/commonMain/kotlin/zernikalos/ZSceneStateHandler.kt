package zernikalos

import kotlin.js.JsExport

@JsExport
interface ZSceneStateHandler {

    fun onReady(sceneContext: ZSceneContext, renderingContext: ZRenderingContext)

    fun onResize(width: Int, height: Int)

    fun onRender(sceneContext: ZSceneContext, renderingContext: ZRenderingContext)
}