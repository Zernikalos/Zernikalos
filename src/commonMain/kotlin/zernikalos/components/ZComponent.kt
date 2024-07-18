/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import zernikalos.context.ZRenderingContext
import zernikalos.logger.ZLoggable
import zernikalos.utils.crc32
import kotlin.js.JsExport

/**
 * Represents a component in Zernikalos.
 *
 * These will encapsulate two different types of components:
 * Basic components: For data storage and sharing
 * Renderizable components: Which will be able to interact with the graphics APIs
 */
interface ZComponent {

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
     * Represents the unique reference identifier for a component.
     *
     * @property refId The unique reference identifier for the component.
     *
     * @see ZComponent
     * @see ZRefComponentSerializer
     * @see ZLoaderContext
     */
    var refId: Int

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
abstract class ZComponentTemplate<D: ZComponentData>
internal constructor(internal val data: D): ZComponent, ZLoggable {
    private var initialized: Boolean = false

    final override val isInitialized: Boolean
        get() = initialized

    override val isRenderizable: Boolean
        get() = false

    private var _refId: Int? = null

    final override var refId: Int
        get() {
            if (_refId != null) {
                return _refId!!
            }
            _refId = computeRefId()
            return _refId!!
        }
        set(value) {
            _refId = value
        }

    final override fun initialize(ctx: ZRenderingContext) {
        if (initialized) {
            return
        }
        initialized = true

        internalInitialize(ctx)
    }

    protected open fun internalInitialize(ctx: ZRenderingContext) {

    }

    override fun toString(): String {
        return data.toString()
    }

    private fun computeRefId(): Int {
        val str = this.toString()
        val dataArray = str.encodeToByteArray()
        val hashValue = crc32(dataArray)
        return if (hashValue < 0) hashValue.inv() else hashValue
    }
}

/**
 * Represents a template for a Renderizable component in Zernikalos.
 *
 * @param R The type of ZComponentRender associated with the template
 * @property renderer The ZComponentRender associated with the component. Throws an error if the component has not been initialized prior to access the renderer
 */
abstract class ZRenderizableComponentTemplate<
    D: ZComponentData,
    R: ZComponentRender<D>>
internal constructor(data: D): ZComponentTemplate<D>(data) {

    private var _renderer: R? = null

    val renderer: R
        get() {
            if (!isInitialized || _renderer == null) {
                throw Error("The component has not been initialized prior to access the renderer")
            }
            return _renderer!!
        }

    override val isRenderizable: Boolean
        get() = _renderer != null

    override fun internalInitialize(ctx: ZRenderingContext) {
        _renderer = createRenderer(ctx)
        _renderer?.initialize()
    }

    protected abstract fun createRenderer(ctx: ZRenderingContext): R?

}

@JsExport
@Serializable
abstract class ZComponentData: ZLoggable {

    abstract override fun toString(): String

}

@JsExport
abstract class ZComponentRender<D: ZComponentData>(protected val ctx: ZRenderingContext, protected val data: D): ZLoggable {

    abstract fun initialize()

    open fun bind() {}

    open fun unbind() {}

    open fun render() {}
}

// typealias ZBasicComponent<D> = ZRenderizableComponentTemplate<D, ZComponentRender<D>>

class ZEmptyComponentData: ZComponentData() {
    override fun toString(): String {
        return ""
    }
}
// class ZRenderOnlyComponent<R: ZComponentRender<ZEmptyComponentData>>() : ZRenderizableComponentTemplate<ZEmptyComponentData, R>(ZEmptyComponentData())


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
        value as ZComponentTemplate<D>
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
