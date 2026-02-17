package zernikalos.ui

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import zernikalos.events.ZEventQueue

open class ZernikalosView: GLSurfaceView, ZSurfaceView {

    private val nativeRenderer: AndroidNativeRenderer = AndroidNativeRenderer()
    private val touchEventConverter = AndroidTouchEventConverter()

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

    override var eventQueue: ZEventQueue? = null
        set(value) {
            field = value
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }

        val queue = eventQueue
        if (queue != null) {
            val touchEvents = touchEventConverter.convert(event)
            for (touchEvent in touchEvents) {
                queue.enqueueTouch(touchEvent)
            }
            return true
        }

        return super.onTouchEvent(event)
    }

    override fun dispose() {
        touchEventConverter.clear()
        nativeRenderer.dispose()
        onPause()
    }
}
