/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
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
import zernikalos.utils.crc32FromStr
import zernikalos.utils.randomNumId
import kotlin.js.JsExport

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

@JsExport
interface ZRef {
    /**
     * Represents the unique reference identifier for a component.
     *
     * @property refId The unique reference identifier for the component.
     *
     * @see ZComponent
     * @see ZRefComponentSerializer
     * @see ZLoaderContext
     */
    val refId: Int
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
abstract class ZBaseComponent(
    protected val internalData: ZComponentData? = null
): ZComponent {

    final override val refId: Int by lazy {
        internalData?.refId ?: randomNumId()
    }

    private var _renderer: ZBaseComponentRender? = null

    protected val internalRenderer: ZBaseComponentRender
        get() {
            if (!isInitialized || !isRenderizable) {
                throw Error("The component has not been initialized prior to access the renderer")
            }
            return _renderer!!
        }

    override val isRenderizable: Boolean
        get() = _renderer != null

    private var initialized: Boolean = false

    final override val isInitialized: Boolean
        get() = initialized

    final override fun initialize(ctx: ZRenderingContext) {
        if (initialized) {
            return
        }
        initialized = true

        _renderer = createRenderer(ctx)
        _renderer?.initialize()
        internalInitialize(ctx)
    }

    internal open fun createRenderer(ctx: ZRenderingContext): ZBaseComponentRender? {
        return null
    }

    protected open fun internalInitialize(ctx: ZRenderingContext) {

    }

}

abstract class ZSerializableComponent<D: ZComponentData>(data: D): ZBaseComponent(data) {
    @Suppress("UNCHECKED_CAST")
    internal val data: D
        get() = internalData as D
}

abstract class ZRenderizableComponent<R: ZBaseComponentRender>: ZBaseComponent() {
    @Suppress("UNCHECKED_CAST")
    val renderer: R
        get() = internalRenderer as R
}

abstract class ZTemplateComponent<D: ZComponentData, R: ZComponentRender<D>>(data: D): ZBaseComponent(data) {
    @Suppress("UNCHECKED_CAST")
    internal val data: D
        get() = internalData as D

    @Suppress("UNCHECKED_CAST")
    val renderer: R
        get() = internalRenderer as R
}

/**
 * Represents a template for a Renderizable component in Zernikalos.
 *
 * @param R The type of ZComponentRender associated with the template
 * @property renderer The ZComponentRender associated with the component. Throws an error if the component has not been initialized prior to access the renderer
 */

@JsExport
abstract class ZComponentData: ZLoggable, ZRef {

    private var _refId: Int? = null

    @Transient
    override val refId: Int
        get() {
            if (_refId == null) {
                _refId = computeRefId()
            }
            return _refId!!
        }

    private fun computeRefId(): Int {
        val hashValue = crc32FromStr(toString())
        return if (hashValue < 0) hashValue.inv() else hashValue
    }

    abstract override fun toString(): String

}

@JsExport
abstract class ZBaseComponentRender(protected val ctx: ZRenderingContext): ZLoggable {

    abstract fun initialize()

    open fun bind() {}

    open fun unbind() {}

    open fun render() {}
}

@JsExport
abstract class ZComponentRender<D: ZComponentData>(ctx: ZRenderingContext, protected val data: D): ZBaseComponentRender(ctx)

abstract class ZComponentSerializer<
    T: ZComponent,
    D: ZComponentData>
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
