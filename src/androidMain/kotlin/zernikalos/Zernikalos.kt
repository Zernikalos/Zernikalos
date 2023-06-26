package zernikalos

import android.opengl.GLSurfaceView
import zernikalos.ui.ZSurfaceView

class Zernikalos: ZernikalosBase() {

    fun initialize(view: GLSurfaceView, stateHandler: ZSceneStateHandler) {
        val surfaceView = ZSurfaceView(view)
        initialize(surfaceView, stateHandler)
    }

}
