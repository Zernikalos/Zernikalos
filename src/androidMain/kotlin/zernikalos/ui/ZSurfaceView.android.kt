/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

actual class ZSurfaceView(view: GLSurfaceView) {

    var nativeSurfaceView: GLSurfaceView
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
        this.nativeSurfaceView = view
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
//        // TODO: Review this
//        GLES20.glViewport(0, 0, width, height)
        eventHandler?.onResize(width, height)
    }

    override fun onDrawFrame(p0: GL10?) {
        eventHandler?.onRender()
    }
}
