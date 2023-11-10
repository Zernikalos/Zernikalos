package zernikalos

import android.opengl.GLSurfaceView
import zernikalos.context.createDefaultContextCreator
import zernikalos.ui.ZSurfaceView

class Zernikalos: ZernikalosBase() {

    fun initialize(view: GLSurfaceView, stateHandler: ZSceneStateHandler) {
        val surfaceView = ZSurfaceView(view)
        initialize(surfaceView, stateHandler)
    }

    fun initialize(view: ZSurfaceView, stateHandler: ZSceneStateHandler) {
        val contextCreator = createDefaultContextCreator()

        initialize(view, contextCreator, stateHandler)
    }

}
