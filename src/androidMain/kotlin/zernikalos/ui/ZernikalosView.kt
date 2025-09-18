package zernikalos.ui

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

open class ZernikalosView: GLSurfaceView, ZSurfaceView {

    private val nativeRenderer: AndroidNativeRenderer = AndroidNativeRenderer()

    override val surfaceWidth: Int
        get() {
            return width
        }

    override val surfaceHeight: Int
        get() {
            return height
        }

    override var eventHandler: ZSurfaceViewEventHandler?
        get() = nativeRenderer.eventHandler
        set(value) {
            nativeRenderer.eventHandler = value
        }

    constructor(context: Context): super(context) {
        if (!isInEditMode) {
            setNativeRenderer();
        }
    }

    constructor(context: Context, attrSet: AttributeSet): super(context, attrSet) {
        if (!isInEditMode) {
            setNativeRenderer();
        }
    }

    private fun setNativeRenderer() {
        setEGLContextClientVersion(3)
        setRenderer(nativeRenderer)

        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        preserveEGLContextOnPause = true
    }

    override fun dispose() {
        nativeRenderer.dispose()
        onPause()
    }
}
