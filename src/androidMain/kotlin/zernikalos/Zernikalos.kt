package zernikalos

import android.opengl.GLSurfaceView
import zernikalos.ui.ZSurfaceView

class Zernikalos: ZernikalosBase() {

    fun attachView(view: GLSurfaceView) {
        val surfaceView = ZSurfaceView()
        surfaceView.attachView(view)

        attachSurfaceView(surfaceView)
    }

}