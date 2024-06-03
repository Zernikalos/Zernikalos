/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos

import zernikalos.context.ZContext
import zernikalos.ui.ZSurfaceViewEventHandler

enum class ZSurfaceViewEventHandlerState {
    READY,
    INITIALIZING,
    HANDLER_INITIALIZED,
    SCENE_INITIALIZED
}

fun createSurfaceViewEventHandler(context: ZContext, stateHandler: ZSceneStateHandler): ZSurfaceViewEventHandler {
    return object: ZSurfaceViewEventHandler {

        var initializationState: ZSurfaceViewEventHandlerState = ZSurfaceViewEventHandlerState.READY

        private fun doneInitialize() {
            initializationState = ZSurfaceViewEventHandlerState.HANDLER_INITIALIZED
        }

        private fun doneRender() {
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
                context.scene?.initialize(context)
            }
        }

        private fun render() {
            if (initializationState == ZSurfaceViewEventHandlerState.SCENE_INITIALIZED) {
                stateHandler.onRender(context, ::doneRender)
                context.scene?.render(context)
            }
        }

        override fun onReady() {
            ready()
            initialize()
        }

        override fun onRender() {
            ready()
            initialize()
            render()
        }
    }
}