package zernikalos.ui

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

actual class ZSurfaceView(view: GLSurfaceView) {

    lateinit var nativeSurfaceView: GLSurfaceView
    private val nativeRenderer: AndroidNativeRenderer = AndroidNativeRenderer()

    actual val width: Int
        get() = nativeSurfaceView.width
    actual val height: Int
        get() = nativeSurfaceView.height
    actual var eventHandler: ZSurfaceViewEventHandler?
        get() = nativeRenderer.eventHandler
        set(value) {
            nativeRenderer.eventHandler = value
        }

    init {
        attachView(view)
    }

    fun attachView(nativeSurfaceView: GLSurfaceView) {
        this.nativeSurfaceView = nativeSurfaceView

        setNativeRenderer()
    }

    private fun setNativeRenderer() {
        nativeSurfaceView.setEGLContextClientVersion(3)
        nativeSurfaceView.setRenderer(nativeRenderer)

        nativeSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        nativeSurfaceView.preserveEGLContextOnPause = true
    }
}

class AndroidNativeRenderer: GLSurfaceView.Renderer {

    var eventHandler: ZSurfaceViewEventHandler? = null

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        eventHandler?.onReady()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(p0: GL10?) {
        eventHandler?.onRender()
    }
}
