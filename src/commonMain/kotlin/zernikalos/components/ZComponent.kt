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
import kotlin.js.JsExport

@JsExport
open class ZComponent<
    D: ZComponentData,
    R: ZComponentRender<D>>
internal constructor(internal val data: D): ZLoggable {

    private var initialized: Boolean = false

    val isInitialized: Boolean
        get() = initialized

    private var _renderer: R? = null

    val renderer: R
        get() {
            if (!initialized || _renderer == null) {
                throw Error("The component has not been initialized prior to access the renderer")
            }
            return _renderer!!
        }

    val isRenderizable: Boolean
        get() = _renderer != null

    fun initialize(ctx: ZRenderingContext) {
        if (initialized) {
            return
        }
        _renderer = createRenderer(ctx)
        initialized = true

        internalInitialize()
    }

    protected open fun internalInitialize() {
        _renderer?.initialize()
    }

    protected open fun createRenderer(ctx: ZRenderingContext): R? {
        return null
    }

    override fun toString(): String {
        return data.toString()
    }

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

typealias ZBasicComponent<D> = ZComponent<D, ZComponentRender<D>>

class ZEmptyComponentData: ZComponentData() {
    override fun toString(): String {
        return ""
    }
}
class ZRenderOnlyComponent<R: ZComponentRender<ZEmptyComponentData>>() : ZComponent<ZEmptyComponentData, R>(ZEmptyComponentData())


abstract class ZComponentSerializer<
    T: ZComponent<D, *>,
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
