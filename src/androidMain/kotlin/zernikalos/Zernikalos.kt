package zernikalos

import android.opengl.GLSurfaceView
import zernikalos.ui.ZkSurfaceView

class Zernikalos: ZernikalosBase() {

    fun attachView(view: GLSurfaceView) {
        val surfaceView = ZkSurfaceView()
        surfaceView.attachView(view)

        attachSurfaceView(surfaceView)
    }

}