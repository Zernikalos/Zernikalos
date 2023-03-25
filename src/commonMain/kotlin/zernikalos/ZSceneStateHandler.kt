package zernikalos

import kotlin.js.JsName

interface ZSceneStateHandler {

    @JsName("createSceneContext")
    fun createSceneContext(): ZSceneContext

    @JsName("onReady")
    fun onReady(sceneContext: ZSceneContext, renderingContext: ZRenderingContext)

    fun onResize(width: Int, height: Int)

    @JsName("onRender")
    fun onRender(sceneContext: ZSceneContext, renderingContext: ZRenderingContext)
}