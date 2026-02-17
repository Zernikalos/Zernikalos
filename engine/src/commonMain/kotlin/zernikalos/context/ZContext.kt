/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context

import zernikalos.events.ZEventQueue
import zernikalos.events.ZInputState
import zernikalos.objects.ZCamera
import zernikalos.objects.ZScene
import zernikalos.utils.genRefId
import kotlin.js.JsExport

// TODO: sceneContext and renderingContext must be internal
@JsExport
class ZContext(val sceneContext: ZSceneContext, val renderingContext: ZRenderingContext) {

    val refId: String = genRefId()

    var screenWidth: Int = 0
    var screenHeight: Int = 0

    var scene: ZScene? by sceneContext::scene

    var activeCamera: ZCamera? by sceneContext::activeCamera

    val isInitialized: Boolean by sceneContext::isInitialized

    /**
     * Event queue that accumulates user input events and processes them synchronously
     * during the game loop frame update phase.
     */
    val eventQueue: ZEventQueue = ZEventQueue(this)

    /**
     * Input state container that tracks the current state of all input devices
     * (keyboard, mouse, touch, etc.).
     *
     * The state is automatically updated by the event system when input events
     * are processed during the game loop frame update phase.
     *
     * Use this to query the current state of input devices, similar to Unity's `Input`,
     * Unreal's input system, or Godot's `Input` singleton.
     *
     * @example
     * ```kotlin
     * // Check keyboard state
     * if (context.input.keyboard.isKeyPressed(ZKeyCode.Space)) {
     *     // Space bar is being held down
     * }
     *
     * // Future: mouse and touch states
     * // if (context.input.mouse.isButtonPressed(...)) { ... }
     * // if (context.input.touch.isTouching(...)) { ... }
     * ```
     */
    val input: ZInputState = ZInputState()
}
