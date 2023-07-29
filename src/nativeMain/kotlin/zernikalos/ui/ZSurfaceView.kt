package zernikalos.ui

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.MetalKit.MTKView
import zernikalos.ZRenderingContext

actual class ZSurfaceView(view: MTKView) {

    var nativeView: MTKView

    @OptIn(ExperimentalForeignApi::class)
    actual val width: Int
        get() {
            nativeView.frame.useContents {
                val widthValue = this.size.width
                return widthValue.toInt()
            }
        }
    @OptIn(ExperimentalForeignApi::class)
    actual val height: Int
        get() {
            nativeView.frame.useContents {
                val heightValue = this.size.height
                return heightValue.toInt()
            }
        }

    actual var eventHandler: ZSurfaceViewEventHandler? = null

    init {
        nativeView = view
    }

}