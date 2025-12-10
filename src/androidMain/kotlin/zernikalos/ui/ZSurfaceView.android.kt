/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

import android.opengl.GLSurfaceView
import zernikalos.events.ZEventQueue

class ZAndroidSurfaceView(view: GLSurfaceView): ZSurfaceView {

    var nativeSurfaceView: GLSurfaceView
    private val nativeRenderer: AndroidNativeRenderer = AndroidNativeRenderer()
    private val touchEventConverter = AndroidTouchEventConverter()

    override val surfaceWidth: Int
        get() = nativeSurfaceView.width

    override val surfaceHeight: Int
        get() = nativeSurfaceView.height

    override var eventHandler: ZSurfaceViewEventHandler?
        get() = nativeRenderer.eventHandler
        set(value) {
            nativeRenderer.eventHandler = value
        }

    override var eventQueue: ZEventQueue? = null
        set(value) {
            field = value
            setupTouchListener()
        }

    init {
        this.nativeSurfaceView = view
        setNativeRenderer()
        setupTouchListener()
    }

    private fun setNativeRenderer() {
        nativeSurfaceView.setEGLContextClientVersion(3)
        nativeSurfaceView.setRenderer(nativeRenderer)

        nativeSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        nativeSurfaceView.preserveEGLContextOnPause = true
    }

    private fun setupTouchListener() {
        nativeSurfaceView.setOnTouchListener { _, event ->
            val queue = eventQueue
            if (queue != null && event != null) {
                val touchEvents = touchEventConverter.convert(event)
                for (touchEvent in touchEvents) {
                    queue.enqueueTouch(touchEvent)
                }
                true
            } else {
                false
            }
        }
    }

    override fun dispose() {
        touchEventConverter.clear()
        nativeSurfaceView.setOnTouchListener(null)
        nativeRenderer.dispose()
        nativeSurfaceView.onPause()
    }
}
