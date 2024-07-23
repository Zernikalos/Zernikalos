/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos

import zernikalos.context.ZContext
import zernikalos.renderer.ZRenderer
import zernikalos.ui.ZSurfaceViewEventHandler

enum class ZSurfaceViewEventHandlerState {
    READY,
    INITIALIZING,
    HANDLER_INITIALIZED,
    SCENE_INITIALIZED
}

fun createSurfaceViewEventHandler(context: ZContext, stateHandler: ZSceneStateHandler): ZSurfaceViewEventHandler {
    return object: ZSurfaceViewEventHandler {

        private val mainRenderer: ZRenderer = ZRenderer(context)

        var initializationState: ZSurfaceViewEventHandlerState = ZSurfaceViewEventHandlerState.READY

        private fun doneInitialize() {
            initializationState = ZSurfaceViewEventHandlerState.HANDLER_INITIALIZED
        }

        private var requiresResizing = false

        // TODO: Review these functions
        private fun doneRender() {
        }

        private fun doneResize() {
        }

        private fun ready() {
            if (initializationState == ZSurfaceViewEventHandlerState.READY) {
                initializationState = ZSurfaceViewEventHandlerState.INITIALIZING
                stateHandler.onReady(context, ::doneInitialize)
            }
        }

        private fun initialize() {
            if (initializationState == ZSurfaceViewEventHandlerState.HANDLER_INITIALIZED) {
                initializationState = ZSurfaceViewEventHandlerState.SCENE_INITIALIZED
                mainRenderer.initialize()
            }
        }

        private fun render() {
            if (initializationState == ZSurfaceViewEventHandlerState.SCENE_INITIALIZED) {
                stateHandler.onRender(context, ::doneRender)
                mainRenderer.render()
            }
        }

        private fun resize(width: Int, height: Int) {
            context.screenWidth = width
            context.screenHeight = height
            if (initializationState == ZSurfaceViewEventHandlerState.SCENE_INITIALIZED) {
                stateHandler.onResize(context, width, height, ::doneResize)
                mainRenderer.onViewportResize(width, height)
                requiresResizing = false
            } else {
                requiresResizing = true
            }
        }

        override fun onReady() {
            ready()
            initialize()
            if (requiresResizing) {
                resize(context.screenWidth, context.screenHeight)
            }
        }

        override fun onRender() {
            ready()
            initialize()
            if (requiresResizing) {
                resize(context.screenWidth, context.screenHeight)
            }
            render()
        }

        override fun onResize(width: Int, height: Int) {
            ready()
            initialize()
            resize(width, height)
        }
    }
}