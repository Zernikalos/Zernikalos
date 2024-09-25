/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.view.View.OnTouchListener
import zernikalos.events.ZAndroidEventConverter
import zernikalos.events.ZTouchEvent
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ZAndroidSurfaceView(view: GLSurfaceView): ZSurfaceView {

    var nativeSurfaceView: GLSurfaceView
    private val nativeRenderer: AndroidNativeRenderer = AndroidNativeRenderer()

    override val surfaceWidth: Int
        get() = nativeSurfaceView.width

    override val surfaceHeight: Int
        get() = nativeSurfaceView.height

    override var eventHandler: ZSurfaceViewEventHandler?
        get() = nativeRenderer.eventHandler
        set(value) {
            nativeRenderer.eventHandler = value
        }

    private val nativeRenderer: AndroidNativeRenderer = AndroidNativeRenderer()

    private val androidEventConverter: ZAndroidEventConverter = ZAndroidEventConverter()

    private var lastTouchEvent: ZTouchEvent? = null

    init {
        this.nativeSurfaceView = view
        setNativeRenderer()
        setNativeViewEventHandlers()
    }

    private fun setNativeRenderer() {
        nativeSurfaceView.setEGLContextClientVersion(3)
        nativeSurfaceView.setRenderer(nativeRenderer)

        nativeSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        nativeSurfaceView.preserveEGLContextOnPause = true
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setNativeViewEventHandlers() {
        nativeSurfaceView.setOnTouchListener { view, motionEvent ->
            val zevent = androidEventConverter.convertMotionEvent(motionEvent, lastTouchEvent)
            eventHandler?.onEvent(zevent)
            lastTouchEvent = zevent
            true
        }

        nativeSurfaceView.setOnClickListener {

        }
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
