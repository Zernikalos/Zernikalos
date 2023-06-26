package zernikalos.ui

import zernikalos.ZRenderingContext

expect class ZSurfaceView {

    val width: Int
    val height: Int

    var eventHandler: ZSurfaceViewEventHandler?
}