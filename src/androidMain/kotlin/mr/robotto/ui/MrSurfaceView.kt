package mr.robotto.ui

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class MrSurfaceView: GLSurfaceView {

    private val renderer: MrRenderer = MrRenderer()

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    init {
        if (!isInEditMode) {
            setRenderer()
        }
    }

    private fun setRenderer() {
        setEGLContextClientVersion(3)
        setRenderer(renderer)

        renderMode = RENDERMODE_CONTINUOUSLY
        preserveEGLContextOnPause = true
    }

}