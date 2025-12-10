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

/**
 * Implementation of the surface view event handler that coordinates
 * the initialization, rendering, and resizing of the view.
 */
private class ZSurfaceViewEventHandlerImpl(
    private val context: ZContext,
    private val stateHandler: ZSceneStateHandler
): ZSurfaceViewEventHandler {

    private val renderer: ZRenderer = ZRenderer(context)
    private val initProcess = InitializationProcess()

    private var isRendering = false
    private var pendingRender = false


    override fun onReady() {
        progressInitialization()
    }

    override fun onRender() {
        progressInitialization()
        if (initProcess.isReady) {
            performRender()
        }
    }

    override fun onResize(width: Int, height: Int) {
        updateScreenDimensions(width, height)

        if (initProcess.isReady) {
            applyResize(width, height)
        } else {
            initProcess.requestResize()
        }
    }

    /**
     * Advances the initialization process according to the current state.
     */
    private fun progressInitialization() {
        when {
            initProcess.shouldStartSceneHandler -> {
                initProcess.goToStateSceneHandlerInitialization() // Move to SCENE_HANDLER_INITIALIZING
                stateHandler.onReady(context) {
                    initProcess.goToStateRendererInitialization() // Move to RENDERER_INITIALIZING
                }
            }
            initProcess.shouldInitializeRenderer -> {
                renderer.initialize()
                initProcess.goToStateReady() // Move to READY

                // Apply resize if requested during initialization
                if (initProcess.hasResizeRequest) {
                    applyResize(context.screenWidth, context.screenHeight)
                    initProcess.clearResizeRequest()
                }
            }
            // No actions needed for other states
        }
    }

    /**
     * Executes the rendering process.
     */
    private fun performRender() {
        if (isRendering) {
            pendingRender = true
            return
        }

        isRendering = true
        pendingRender = false

        stateHandler.onUpdate(context) {
            renderer.render()
            isRendering = false

            if (pendingRender) {
                performRender()
            }
        }
    }

    /**
     * Updates the context dimensions.
     */
    private fun updateScreenDimensions(width: Int, height: Int) {
        context.screenWidth = width
        context.screenHeight = height
    }

    /**
     * Applies the resize to the renderer and notifies the state handler.
     */
    private fun applyResize(width: Int, height: Int) {
        stateHandler.onResize(context, width, height) {
            renderer.onViewportResize(width, height)
        }
    }

}

/**
 * A process manager that handles the sequential initialization steps
 * and keeps track of resize requests that need to be applied once
 * initialization is complete.
 */
private class InitializationProcess {
    /**
     * The possible initialization states, in sequential order.
     */
    private enum class State {
        NOT_STARTED,
        SCENE_HANDLER_INITIALIZING,
        RENDERER_INITIALIZING,
        READY
    }

    private var currentState = State.NOT_STARTED

    /**
     * Tracks whether there is a pending resize request that needs
     * to be applied once initialization completes.
     */
    private var resizeRequest = false

    fun goToStateSceneHandlerInitialization() {
        if (currentState == State.NOT_STARTED) {
            nextState()
        }
    }

    fun goToStateRendererInitialization() {
        if (currentState == State.SCENE_HANDLER_INITIALIZING) {
            nextState()
        }
    }

    fun goToStateReady() {
        if (currentState == State.RENDERER_INITIALIZING) {
            nextState()
        }
    }

    /**
     * Advances to the next state in the initialization sequence.
     * States can only progress forward, never backward.
     */
    private fun nextState() {
        currentState = when (currentState) {
            State.NOT_STARTED -> State.SCENE_HANDLER_INITIALIZING
            State.SCENE_HANDLER_INITIALIZING -> State.RENDERER_INITIALIZING
            State.RENDERER_INITIALIZING -> State.READY
            State.READY -> throw IllegalStateException("Already at final state, cannot progress further")
        }
    }

    /**
     * Records that a resize request has been made during initialization.
     */
    fun requestResize() {
        if (!isReady) {
            resizeRequest = true
        }
    }

    /**
     * Clears any pending resize request, typically after it has been applied.
     */
    fun clearResizeRequest() {
        resizeRequest = false
    }

    /* State query getters */

    /**
     * Whether we should begin initializing the scene handler.
     */
    val shouldStartSceneHandler: Boolean
        get() = currentState == State.NOT_STARTED

    /**
     * Whether we should initialize the renderer.
     */
    val shouldInitializeRenderer: Boolean
        get() = currentState == State.RENDERER_INITIALIZING

    /**
     * Whether the initialization process is complete and system is ready.
     */
    val isReady: Boolean
        get() = currentState == State.READY

    /**
     * Whether there is a pending resize request to be applied.
     */
    val hasResizeRequest: Boolean
        get() = resizeRequest
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
