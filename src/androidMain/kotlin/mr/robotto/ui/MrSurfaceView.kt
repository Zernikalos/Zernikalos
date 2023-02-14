package mr.robotto.ui

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import mr.robotto.MrRenderingContext
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

actual class MrSurfaceView {

    actual val stateHandlerBridge: MrSurfaceStateHandlerBridge = MrSurfaceStateHandlerBridge()
    actual val renderingContext: MrRenderingContext
        get() = nativeRenderer.renderingContext

    lateinit var nativeSurfaceView: GLSurfaceView
    private val nativeRenderer: AndroidNativeRenderer = AndroidNativeRenderer(stateHandlerBridge)

    actual val width: Int
        get() = nativeSurfaceView.width
    actual val height: Int
        get() = nativeSurfaceView.height

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

class AndroidNativeRenderer(private val stateHandlerBridge: MrSurfaceStateHandlerBridge) : GLSurfaceView.Renderer {

    val renderingContext: MrRenderingContext = MrRenderingContext()

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        stateHandlerBridge.onReady(renderingContext)
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(p0: GL10?) {
        stateHandlerBridge.onRender(renderingContext)
    }
}
