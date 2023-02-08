package mr.robotto

import kotlin.js.JsName

interface MrSceneStateHandler {

    @JsName("createSceneContext")
    fun createSceneContext(): MrSceneContext

    @JsName("onReady")
    fun onReady(sceneContext: MrSceneContext, renderingContext: MrRenderingContext)

    fun onResize(width: Int, height: Int)

    @JsName("onRender")
    fun onRender(sceneContext: MrSceneContext, renderingContext: MrRenderingContext)
}