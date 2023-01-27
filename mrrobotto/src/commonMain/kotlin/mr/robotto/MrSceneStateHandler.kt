package mr.robotto

import kotlin.js.JsName

interface MrSceneStateHandler {

    @JsName("onReady")
    fun onReady(renderingContext: MrRenderingContext)

    fun onResize(width: Int, height: Int)

    fun onRender()
}