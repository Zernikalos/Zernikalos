package zernikalos.ui

import zernikalos.ZRenderingContext

expect class ZSurfaceView {

    val width: Int
    val height: Int

    val stateHandlerBridge: ZSurfaceStateHandlerBridge
    val renderingContext: ZRenderingContext
}