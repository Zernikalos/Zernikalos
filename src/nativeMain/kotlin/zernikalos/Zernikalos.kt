package zernikalos

import platform.MetalKit.MTKView
import zernikalos.ui.ZSurfaceView

class Zernikalos: ZernikalosBase() {

    fun initialize(view: MTKView, stateHandler: ZSceneStateHandler) {
        val surfaceView = ZSurfaceView(view)
        initializeWithDefaults(surfaceView, stateHandler)
    }

    fun initializeWithDefaults(view: ZSurfaceView, stateHandler: ZSceneStateHandler) {
        val contextCreator = createDefaultContextCreator()

        initialize(view, contextCreator, stateHandler)
    }

}