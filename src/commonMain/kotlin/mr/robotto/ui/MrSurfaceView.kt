package mr.robotto.ui

import mr.robotto.MrRenderingContext

expect class MrSurfaceView {

    val width: Int
    val height: Int

    val stateHandlerBridge: MrSurfaceStateHandlerBridge
    val renderingContext: MrRenderingContext

    fun onReady()
}