package zernikalos

import kotlin.js.JsName

interface ZkSceneStateHandler {

    @JsName("createSceneContext")
    fun createSceneContext(): ZkSceneContext

    @JsName("onReady")
    fun onReady(sceneContext: ZkSceneContext, renderingContext: ZkRenderingContext)

    fun onResize(width: Int, height: Int)

    @JsName("onRender")
    fun onRender(sceneContext: ZkSceneContext, renderingContext: ZkRenderingContext)
}