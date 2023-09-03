package zernikalos.ui

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.MetalKit.MTKView

actual class ZSurfaceView(view: MTKView) {

    var nativeView: MTKView

    @OptIn(ExperimentalForeignApi::class)
    actual val width: Int
        get() {
            nativeView.drawableSize().useContents {
                val widthValue = this.width
                return widthValue.toInt()
            }
        }
    @OptIn(ExperimentalForeignApi::class)
    actual val height: Int
        get() {
            nativeView.drawableSize.useContents {
                val heightValue = this.height
                return heightValue.toInt()
            }
        }

    actual var eventHandler: ZSurfaceViewEventHandler? = null

    init {
        nativeView = view
    }

}