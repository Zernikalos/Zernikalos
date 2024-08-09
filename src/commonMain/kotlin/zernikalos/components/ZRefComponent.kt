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
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.loader.ZLoaderContext

interface ZRefComponentWrapper<D: ZComponentData> {
    @ProtoNumber(1)
    var refId: Int

    @ProtoNumber(2)
    var isReference: Boolean

    @ProtoNumber(100)
    var data: D?
}

abstract class ZRefComponentSerializer<
    T: ZComponent,
    D: ZComponentData,
    K: ZRefComponentWrapper<D>
    >(protected val loaderContext: ZLoaderContext)
    : KSerializer<T> {

    abstract val deserializationStrategy: DeserializationStrategy<K>

    override val descriptor: SerialDescriptor
        get() = deserializationStrategy.descriptor

    protected abstract fun createComponentInstance(data: D): T

    override fun deserialize(decoder: Decoder): T {
        val refData = decoder.decodeSerializableValue(deserializationStrategy)

        if (refData.isReference) {
            @Suppress("UNCHECKED_CAST")
            return loaderContext.getComponent(refData.refId)!! as T
        }

        val component = createComponentInstance(refData.data!!)

        loaderContext.addComponent(refData.refId, component)
        return component
    }

    override fun serialize(encoder: Encoder, value: T) {}

}
