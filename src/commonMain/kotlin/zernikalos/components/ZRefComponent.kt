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
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import zernikalos.loader.ZLoaderContext
import zernikalos.utils.crc32
import kotlin.js.JsExport

@JsExport
interface ZRef {
    var refId: Int
}

@JsExport
abstract class ZRefComponent<D: ZComponentData, R: ZComponentRender<D>> internal constructor(data: D): ZComponent<D, R>(data), ZRef {
    private var _refId: Int? = null

    override var refId: Int
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

    private fun computeRefId(): Int {
        return computeRefIdFromString(toString())
    }

    override fun toString(): String {
        return data.toString()
    }
}

fun computeRefIdFromString(str: String): Int {
    val dataArray = str.encodeToByteArray()
    val hashValue = crc32(dataArray)
    return if (hashValue < 0) hashValue.inv() else hashValue
}

interface ZRefComponentData<D: ZComponentData> {
    var refId: Int
    var isReference: Boolean
    var data: D?
}

abstract class ZRefComponentSerializer<
    T: ZRefComponent<D, *>,
    D: ZComponentData,
    R: ZRefComponentData<D>
    >(protected val loaderContext: ZLoaderContext)
    : KSerializer<T> {

    abstract val deserializationStrategy: DeserializationStrategy<R>

    override val descriptor: SerialDescriptor
        get() = deserializationStrategy.descriptor

    protected abstract fun createComponentInstance(data: D): T

    override fun deserialize(decoder: Decoder): T {
        val refData = decoder.decodeSerializableValue(deserializationStrategy)

        if (refData.isReference) {
            return loaderContext.getComponent(refData.refId) as T
        }

        val component = createComponentInstance(refData.data!!)
        component.refId = refData.refId

        loaderContext.addComponent(refData.refId, component)
        return component
    }

    override fun serialize(encoder: Encoder, value: T) {}

}
