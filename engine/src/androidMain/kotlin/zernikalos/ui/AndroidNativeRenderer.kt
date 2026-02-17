package zernikalos.ui

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @suppress
 * A custom renderer for Android's `GLSurfaceView` that integrates with a [`ZSurfaceViewEventHandler`].
 *
 * This class implements the `GLSurfaceView.Renderer` interface and manages the rendering lifecycle
 * of an OpenGL surface, delegating key events to an external event handler.
 *
 * Functionality includes:
 * - Notifying when the surface is created via [onSurfaceCreated].
 * - Notifying when the surface dimensions change via [onSurfaceChanged].
 * - Triggering rendering operations via [onDrawFrame].
 * - Providing a mechanism to clean up resources via [dispose].
 */
class AndroidNativeRenderer: GLSurfaceView.Renderer {

    var eventHandler: ZSurfaceViewEventHandler? = null

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        eventHandler?.onReady()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        eventHandler?.onResize(width, height)
    }

    override fun onDrawFrame(p0: GL10?) {
        eventHandler?.onRender()
    }

    fun dispose() {
        eventHandler = null
    }
}
