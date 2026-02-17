/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.statehandler

import zernikalos.context.ZContext
import zernikalos.renderer.ZRenderer
import zernikalos.scenestatehandler.ZSceneStateHandler
import zernikalos.ui.ZSurfaceViewEventHandler

private enum class InitState {
    NOT_STARTED,
    SCENE_INIT,
    RENDERER_INIT,
    READY
}

/**
 * Implementation of the surface view event handler that coordinates
 * the initialization, rendering, and resizing of the view.
 */
private class ZSurfaceViewEventHandlerImpl(
    private val context: ZContext,
    private val stateHandler: ZSceneStateHandler
): ZSurfaceViewEventHandler {

    private val renderer: ZRenderer = ZRenderer(context)

    private var initState = InitState.NOT_STARTED
    private var pendingResize = false

    private var isRendering = false
    private var pendingRender = false

    override fun onReady() {
        progressInitialization()
    }

    override fun onRender() {
        progressInitialization()
        if (initState == InitState.READY) {
            performRender()
        }
    }

    override fun onResize(width: Int, height: Int) {
        context.screenWidth = width
        context.screenHeight = height

        if (initState == InitState.READY) {
            applyResize(width, height)
        } else {
            pendingResize = true
        }
    }

    private fun progressInitialization() {
        when (initState) {
            InitState.NOT_STARTED -> {
                initState = InitState.SCENE_INIT
                stateHandler.onReady(context) {
                    initState = InitState.RENDERER_INIT
                }
            }
            InitState.RENDERER_INIT -> {
                renderer.initialize()
                initState = InitState.READY
                if (pendingResize) {
                    applyResize(context.screenWidth, context.screenHeight)
                    pendingResize = false
                }
            }
            InitState.SCENE_INIT, InitState.READY -> { /* no-op */ }
        }
    }

    private fun performRender() {
        if (isRendering) {
            pendingRender = true
            return
        }

        isRendering = true
        pendingRender = false

        stateHandler.onUpdate(context) {
            renderer.update()
        }

        stateHandler.onRender(context) {
            renderer.render()
            isRendering = false
            if (pendingRender) {
                performRender()
            }
        }
    }

    private fun applyResize(width: Int, height: Int) {
        stateHandler.onResize(context, width, height) {
            renderer.onViewportResize(width, height)
        }
    }
}

/**
 * Creates a new instance of the surface view event handler.
 *
 * @param context The application context
 * @param stateHandler The scene state handler
 * @return An implementation of ZSurfaceViewEventHandler
 */
fun createSurfaceViewEventHandler(
    context: ZContext,
    stateHandler: ZSceneStateHandler
): ZSurfaceViewEventHandler = ZSurfaceViewEventHandlerImpl(context, stateHandler)
