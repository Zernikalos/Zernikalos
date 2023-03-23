package zernikalos.ui

import zernikalos.ZkRenderingContext

expect class ZkSurfaceView {

    val width: Int
    val height: Int

    val stateHandlerBridge: ZkSurfaceStateHandlerBridge
    val renderingContext: ZkRenderingContext
}