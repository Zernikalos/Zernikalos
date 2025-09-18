package zernikalos.ui

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AndroidNativeRenderer: GLSurfaceView.Renderer {

    var eventHandler: ZSurfaceViewEventHandler? = null

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        eventHandler?.onReady()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
//        // TODO: Review this
//        GLES20.glViewport(0, 0, width, height)
        eventHandler?.onResize(width, height)
    }

    override fun onDrawFrame(p0: GL10?) {
        eventHandler?.onRender()
    }

    fun dispose() {
        eventHandler = null
    }
}
