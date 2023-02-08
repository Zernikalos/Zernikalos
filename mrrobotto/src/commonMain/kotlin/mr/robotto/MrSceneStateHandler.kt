package mr.robotto

import mr.robotto.objects.MrSceneContext
import kotlin.js.JsName

interface MrSceneStateHandler {

    val sceneContext: MrSceneContext

    @JsName("onReady")
    fun onReady(renderingContext: MrRenderingContext)

    fun onResize(width: Int, height: Int)

    @JsName("onRender")
    fun onRender(renderingContext: MrRenderingContext)
}