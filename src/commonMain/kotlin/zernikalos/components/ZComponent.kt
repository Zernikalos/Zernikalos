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

@JsExport
interface ZRef {

    /**
     * Represents the unique reference identifier for a component.
     *
     * @property refId The unique reference identifier for the component.
     *
     * @see ZComponent
     * @see ZRefComponentSerializer
     */
    val refId: String
}

/**
 * Represents a component in Zernikalos.
 *
 * These will encapsulate two different types of components:
 * Basic components: For data storage and sharing
 * Renderizable components: Which will be able to interact with the graphics APIs
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
 * Represents a template for a basic component in Zernikalos.
 * These are used for storing and sharing data
 *
 * @param D The type of ZComponentData associated with the template
 *
 * @property data The ZComponentData associated with the template*
 * @property isRenderizable Indicates whether the component is renderizable
 *
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
 *
 * Subclasses must override the `toString` method to provide the string representation required for
 * computing the `refId`.
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

interface ZRenderizable {

    /**
     * Draws the mesh on the screen using its renderer.
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
