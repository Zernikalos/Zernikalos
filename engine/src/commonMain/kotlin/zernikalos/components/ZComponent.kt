/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import zernikalos.context.ZRenderingContext
import zernikalos.logger.ZLoggable
import kotlin.js.JsExport
import kotlin.uuid.Uuid

/**
 * Represents a reference entity within the Zernikalos Engine.
 *
 * This interface serves as the foundation for referencing other components
 * by their unique identifier. Components implementing this interface can be
 * used within reference-based systems, enabling efficient retrieval and management.
 *
 * @see ZComponent
 * @see ZComponentData
 */
@JsExport
interface ZRef {

    /**
     * Represents the unique identifier for a reference-based component.
     * The `refId` is used as a distinctive key for retrieving or linking
     * this component within reference-dependent systems.
     */
    val refId: String
}

/**
 * Represents a component in the Zernikalos Engine that provides basic
 * initialization and renderability functionalities. This interface is used
 * as a foundation for defining components that interact with a
 * `ZRenderingContext`.
 *
 * @see ZRef
 * @see ZRenderingContext
 */
@JsExport
interface ZComponent: ZRef {

    /**
     * Represents a boolean value indicating whether a component has been initialized.
     *
     * @property isInitialized Indicates whether the component has been initialized.
     *
     * @see ZComponent
     * @see ZComponent.initialize
     */
    val isInitialized: Boolean

    /**
     * Represents a boolean value indicating whether a component is renderizable.
     *
     * @property isRenderizable Represents whether the component is renderizable.
     *
     * @see ZComponent
     * @see ZComponent.initialize
     */
    val isRenderizable: Boolean

    /**
     * Initializes the ZComponent using the provided ZRenderingContext.
     *
     * @param ctx The ZRenderingContext used for initialization.
     *
     * @see ZComponent
     * @see ZRenderingContext
     */
    fun initialize(ctx: ZRenderingContext)

    fun dispose()
}

/**
 * Contract for components that expose serializable data.
 */
interface ZHasComponentData<out D: Any> {
    val data: D
}

/**
 * Contract for components that expose a renderer instance.
 */
interface ZHasRenderer<out R: ZComponentRenderer> {
    val renderer: R
}

/**
 * Marker interface for render-capable components.
 */
private interface ZRenderCapability

/**
 * Represents the base class for components in the Zernikalos Engine.
 *
 * This abstract class provides foundational properties and methods for
 * components, including initialization, unique identification, and
 * extendable internal initialization logic. It serves as a common ancestor
 * for all component types and implements a capability-based system to avoid
 * code duplication.
 */
abstract class ZBaseComponent(): ZComponent, ZLoggable {

    protected var uuid: Uuid? = null
    final override val refId: String
        get() {
            uuid = uuid ?: Uuid.random()
            return uuid.toString()
        }

    /**
     * Sets the UUID for this component.
     * This method is used internally by mixins to synchronize UUIDs.
     *
     * @param newUuid The UUID to set for this component.
     */
    internal fun setUuid(newUuid: Uuid?) {
        uuid = newUuid
    }

    private var initialized: Boolean = false
    final override val isInitialized: Boolean
        get() = initialized

    private var _disposed = false

    /**
     * Disposes the component and releases resources. Idempotent: multiple calls are safe.
     * Subclasses with own state to release should override [internalDispose].
     */
    final override fun dispose() {
        if (_disposed) return
        _disposed = true
        internalDispose()
    }

    /**
     * Override in subclasses to release component-owned resources. Called at most once by [dispose].
     */
    protected open fun internalDispose() {}

    final override val isRenderizable: Boolean
        get() = this is ZRenderCapability

    /**
     * Sets up the initialization state of the component.
     * This method ensures that initialization only occurs once.
     */
    private fun setupInitialize(): Boolean {
        if (initialized) {
            return false
        }
        initialized = true
        return true
    }

    override fun initialize(ctx: ZRenderingContext) {
        if (!setupInitialize()) {
            return
        }
        internalInitialize(ctx)
    }

    /**
     * Internal initialization method that can be overridden by subclasses
     * to provide custom initialization logic.
     *
     * @param ctx The ZRenderingContext used for initialization.
     */
    protected open fun internalInitialize(ctx: ZRenderingContext) {

    }

}

/**
 * Represents an abstract component in the Zernikalos Engine that is renderizable,
 * meaning it can produce a renderer to handle the rendering logic.
 *
 * Classes inheriting from this component must define how to create their specific
 * renderer type and implement its rendering logic using a provided rendering context.
 *
 * @param R The type of the renderer associated with this component. It must inherit
 *          from ZComponentRenderer.
 */
abstract class ZRenderizableComponent<R: ZComponentRenderer>(): ZBaseComponent(), ZRenderCapability, ZHasRenderer<R> {

    private var _renderer: R? = null

    /**
     * Provides access to the component's renderer.
     *
     * @return The renderer instance for this component.
     * @throws Error if the component has not been initialized.
     */
    final override val renderer: R
        get() {
            if (!isInitialized) {
                throw Error("The component has not been initialized prior to access the renderer")
            }
            return _renderer ?: throw Error("Renderer was requested before it was created")
        }

    override fun initialize(ctx: ZRenderingContext) {
        super.initialize(ctx)
        if (_renderer == null) {
            _renderer = createRenderer(ctx).also { it.initialize() }
        }
    }

    /**
     * Creates a new renderer instance for this component.
     *
     * @param ctx The rendering context used for renderer creation.
     * @return A new renderer instance.
     */
    internal abstract fun createRenderer(ctx: ZRenderingContext): R
}

/**
 * Represents an abstract component in the Zernikalos Engine that can be serialized,
 * meaning it contains data that can be persisted and restored.
 *
 * Classes inheriting from this component must provide a data object that contains
 * the serializable information.
 *
 * @param D The type of the data associated with this component. It must inherit
 *          from ZComponentData.
 */
abstract class ZSerializableComponent<D: ZComponentData>(
    data: D
): ZBaseComponent(), ZHasComponentData<D> {

    /**
     * Provides access to the component's serializable data.
     *
     * @return The data instance for this component.
     */
    final override val data: D = data.also { setUuid(it.uuid) }
}

/**
 * Represents an abstract component in the Zernikalos Engine that combines both
 * rendering and serialization capabilities.
 *
 * This component can be both rendered on screen and serialized to persistent storage,
 * making it ideal for complex objects that need to maintain their visual state
 * across application sessions.
 *
 * Classes inheriting from this component must define how to create their specific
 * renderer type and provide serializable data.
 *
 * @param D The type of the data associated with this component. It must inherit
 *          from ZComponentData.
 * @param R The type of the renderer associated with this component. It must inherit
 *          from ZComponentRenderer.
 */
abstract class ZDataRenderComponent<D: ZComponentData, R: ZComponentRenderer>(
    data: D
): ZSerializableComponent<D>(data), ZRenderCapability, ZHasRenderer<R> {

    private var _renderer: R? = null

    /**
     * Provides access to the component's renderer.
     *
     * @return The renderer instance for this component.
     * @throws Error if the component has not been initialized.
     */
    final override val renderer: R
        get() {
            if (!isInitialized) {
                throw Error("The component has not been initialized prior to access the renderer")
            }
            return _renderer ?: throw Error("Renderer was requested before it was created")
        }

    override fun initialize(ctx: ZRenderingContext) {
        super.initialize(ctx)
        if (_renderer == null) {
            _renderer = createRenderer(ctx).also { it.initialize() }
        }
    }

    /**
     * Creates a new renderer instance for this component.
     *
     * @param ctx The rendering context used for renderer creation.
     * @return A new renderer instance.
     */
    internal abstract fun createRenderer(ctx: ZRenderingContext): R
}

/**
 * Represents an abstract base class for data objects associated with components in the Zernikalos Engine.
 *
 * This class provides a mechanism to calculate and cache a unique reference identifier (`refId`)
 * for the data object, facilitating its usage in serialization, reference management, and
 * other engine functionalities. The `refId` is computed lazily based on the object's string representation.
 *
 * Implementations of this class are commonly used as data containers in component-based architectures
 * within the engine. These subclasses should define specific data structures and properties related
 * to their components.
 */
@JsExport
abstract class ZComponentData: ZLoggable, ZRef {

    internal var uuid: Uuid? = null

    @Transient
    override val refId: String
        get() {
            uuid = uuid ?: Uuid.random()
            return uuid.toString()
        }

    abstract override fun toString(): String

}

/**
 * RENDERER SECTION
 */

/**
 * Abstract base class responsible for rendering components in the Zernikalos engine.
 * Serves as the foundation for implementing render-specific logic for components that
 * require rendering capabilities.
 *
 * This class is associated with a rendering context, which provides the necessary tools
 * and environment for rendering operations. It is designed to be used as a base for
 * component-specific renderers, which must implement the abstract methods and may override
 * existing ones to define custom behavior.
 *
 * @constructor Initializes the renderer with a given rendering context. The constructor
 *              is accessible only within the engine to ensure proper setup and lifecycle
 *              control of renderers.
 *
 * @property ctx The rendering context associated with this renderer. Provides rendering
 *               surfaces and tools required for rendering operations.
 */
@JsExport
abstract class ZComponentRenderer
internal constructor(protected val ctx: ZRenderingContext): ZLoggable {

    abstract fun initialize()

    open fun bind() {}

    open fun unbind() {}

    open fun render() {}

    open fun dispose() {}
}

/**
 * SERIALIZATION SECTION
 */

/**
 * Abstract base class for serializing ZComponent instances.
 *
 * This class provides a framework for converting components to and from
 * serialized data formats. It handles the serialization logic for both
 * ZSerializableComponent and ZDataRenderComponent instances, ensuring proper
 * data persistence and restoration.
 *
 * @param T The type of the component being serialized. Must inherit from ZComponent.
 * @param D The type of the data associated with the component.
 */
abstract class ZComponentSerializer<
    T: ZComponent,
    D: Any>
    : KSerializer<T> {

    /**
     * The serializer for the data type associated with this component.
     */
    abstract val kSerializer: KSerializer<D>

    override val descriptor: SerialDescriptor
        get() = kSerializer.descriptor

    /**
     * Creates a component instance from the provided data.
     *
     * @param data The data to create the component from.
     * @return A new component instance.
     */
    protected abstract fun createComponentInstance(data: D): T

    override fun deserialize(decoder: Decoder): T {
        val data = decoder.decodeSerializableValue(kSerializer)
        return createComponentInstance(data)
    }

    override fun serialize(encoder: Encoder, value: T) {
        if (value !is ZHasComponentData<*>) {
            throw Error("Component does not support serialization")
        }

        @Suppress("UNCHECKED_CAST")
        val data = value.data as D

        encoder.encodeSerializableValue(kSerializer, data)
    }

}

/**
 * Represents a bindable resource or component in the Zernikalos framework.
 * Classes implementing this interface indicate that they require binding prior to usage
 * and unbinding after usage, typically for rendering or resource management purposes.
 */
interface ZBindeable {

    /**
     * Binds the renderer.
     * This method is called to prepare the renderer for drawing.
     */
    fun bind()

    /**
     * Unbinds the renderer.
     * This method is called after drawing to clean up.
     */
    fun unbind()

}

/**
 * Defines a contract for objects that can be rendered on the screen using a rendering system.
 * Implementing classes should provide the logic for drawing their visual representation.
 */
interface ZRenderizable {

    /**
     * Renders the object's visual representation on the screen.
     *
     * Classes implementing the `ZRenderizable` interface must provide
     * their specific rendering logic through the implementation of this method.
     *
     * This method is typically called by the rendering system to draw the
     * current state of the object onto a graphical context.
     */
    fun render()

}

/**
 * An interface for objects that will listen to changes on the viewport dimensions.
 */
interface ZResizable {

    /**
     * Notifies the listener when the viewport dimensions change.
     *
     * @param width The new width of the viewport.
     * @param height The new height of the viewport.
     */
    fun onViewportResize(width: Int, height: Int)
}
