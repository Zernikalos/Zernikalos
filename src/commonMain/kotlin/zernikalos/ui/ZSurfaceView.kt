package zernikalos.ui

expect class ZSurfaceView {

    val width: Int
    val height: Int

    var eventHandler: ZSurfaceViewEventHandler?
}