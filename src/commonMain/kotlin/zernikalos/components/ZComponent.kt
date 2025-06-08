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
 * @see ZRefComponentWrapper
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
}

/**
 * Represents the base class for components in the Zernikalos Engine.
 *
 * This abstract class provides foundational properties and methods for
 * components, including initialization, unique identification, and
 * extendable internal initialization logic. It serves as a common ancestor
 * for both renderizable and non-renderizable components.
 */
abstract class ZBaseComponent(): ZComponent, ZLoggable {

    protected var uuid: Uuid? = null
    final override val refId: String
        get() {
            uuid = uuid ?: Uuid.random()
            return uuid.toString()
        }

    private var initialized: Boolean = false
    final override val isInitialized: Boolean
        get() = initialized

    protected fun setupInitialize() {
        if (initialized) {
            return
        }
        initialized = true
    }

    override fun initialize(ctx: ZRenderingContext) {
        setupInitialize()
        internalInitialize(ctx)
    }

    protected open fun internalInitialize(ctx: ZRenderingContext) {

    }

}

abstract class ZSerializableComponent<D: ZComponentData>(internal val data: D): ZBaseComponent() {
    override val isRenderizable: Boolean = false
    init {
        uuid = data.uuid
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
abstract class ZRenderizableComponent<R: ZComponentRenderer>(): ZBaseComponent() {
    override val isRenderizable: Boolean = true

    private var _renderer: R? = null
    val renderer: R
        get() {
            if (!isInitialized || !isRenderizable) {
                throw Error("The component has not been initialized prior to access the renderer")
            }
            return _renderer!!
        }

    override fun initialize(ctx: ZRenderingContext) {
        setupInitialize()
        if (isRenderizable) {
            _renderer = createRenderer(ctx)
            _renderer?.initialize()
        }
        internalInitialize(ctx)
    }

    abstract fun createRenderer(ctx: ZRenderingContext): R
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
}

/**
 * SERIALIZATION SECTION
 */

abstract class ZComponentSerializer<
    T: ZComponent,
    D: Any>
    : KSerializer<T> {

    abstract val kSerializer: KSerializer<D>

    override val descriptor: SerialDescriptor
        get() = kSerializer.descriptor

    protected abstract fun createComponentInstance(data: D): T

    override fun deserialize(decoder: Decoder): T {
        val data = decoder.decodeSerializableValue(kSerializer)
        return createComponentInstance(data)
    }

    override fun serialize(encoder: Encoder, value: T) {
        @Suppress("UNCHECKED_CAST")
        value as ZSerializableComponent<D>
        return encoder.encodeSerializableValue(kSerializer, value.data)
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
