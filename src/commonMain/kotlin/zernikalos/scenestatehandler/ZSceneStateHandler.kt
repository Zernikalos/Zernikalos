package zernikalos.scenestatehandler

import zernikalos.context.ZContext
import kotlin.js.JsExport

/**
 * Asynchronous state handler for managing the lifecycle events of a scene within the Zernikalos engine.
 * This interface facilitates the handling of ready, resize, and render events in an asynchronous manner
 * by utilizing callback functions to signal the completion of tasks.
 */
@JsExport
interface ZSceneStateHandler {
    /**
     * Called when the scene or the system is ready to start interacting or rendering. This method
     * is typically used to perform initialization tasks specific to the scene state.
     *
     * Uses the callback to specify the end of this operation.
     *
     * @param context The context of the current scene, encapsulating information such as the active
     * camera, rendering context, and scene state.
     * @param done Callback to specify the end of this operation
     */
    fun onReady(context: ZContext, done: () -> Unit)

    /**
     * Handles the resizing of the scene or rendering system, typically triggered when the viewport
     * dimensions change. This should update internal state or configurations based on the new width
     * and height values.
     *
     * Uses the callback to specify the end of this operation.
     *
     * @param context The context of the current scene, containing information about the rendering
     * and scene states.
     * @param width The new width of the viewport or rendering area.
     * @param height The new height of the viewport or rendering area.
     * @param done Callback to specify the end of this operation
     */
    fun onResize(context: ZContext, width: Int, height: Int, done: () -> Unit)

    /**
     * Handles the updating process for the current scene and rendering context.
     * This method is expected to be called during each frame of the rendering loop.
     *
     * Uses the callback to specify the end of this operation.
     *
     * @param context The current scene context encapsulating the state of the scene, active camera,
     * rendering context, and screen dimensions. It provides necessary data required for rendering.
     * @param done Callback to specify the end of this operation
     */
    fun onUpdate(context: ZContext, done: () -> Unit)

    /**
     * Handles the rendering process for the current scene and rendering context.
     * This method is expected to be called during each frame of the rendering loop
     * to process and render the visual elements of the scene.
     *
     * @param context The current scene context encapsulating the state of the scene,
     * active camera, rendering context, and screen dimensions. It provides necessary
     * data required for rendering.
     * @param done Callback to specify the end of this rendering operation.
     */
    fun onRender(context: ZContext, done: () -> Unit)
}

