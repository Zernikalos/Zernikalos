/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components

import kotlinx.serialization.DeserializationStrategy
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
internal constructor(protected val data: D): ZLoggable {

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

    abstract val deserializationStrategy: DeserializationStrategy<D>

    override val descriptor: SerialDescriptor
        get() = deserializationStrategy.descriptor

    protected abstract fun createComponentInstance(data: D): T

    override fun deserialize(decoder: Decoder): T {
        val data = decoder.decodeSerializableValue(deserializationStrategy)
        return createComponentInstance(data)
    }

    override fun serialize(encoder: Encoder, value: T) {}

}

interface ZBindeable {

    fun bind()

    fun unbind()

}

interface ZRenderizable {

    fun render()

}
