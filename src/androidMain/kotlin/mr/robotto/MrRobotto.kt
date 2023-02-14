package mr.robotto

import android.opengl.GLSurfaceView
import mr.robotto.ui.MrSurfaceView

class MrRobotto: MrRobottoBase() {

    fun attachView(view: GLSurfaceView) {
        val surfaceView = MrSurfaceView()
        surfaceView.attachView(view)

        attachSurfaceView(surfaceView)
    }

}