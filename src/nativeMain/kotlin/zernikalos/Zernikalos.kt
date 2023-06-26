package zernikalos

import platform.MetalKit.MTKView
import zernikalos.ui.ZSurfaceView

class Zernikalos: ZernikalosBase() {

    fun initialize(view: MTKView, stateHandler: ZSceneStateHandler) {
        val surfaceView = ZSurfaceView(view)
        initialize(surfaceView, stateHandler)
    }

}