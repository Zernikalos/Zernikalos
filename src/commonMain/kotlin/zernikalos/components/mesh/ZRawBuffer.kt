/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.ZBasicComponent
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentSerializer
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Raw buffer representing the data array with vertex information stored
 */
@Serializable(with = ZRawBufferSerializer::class)
@JsExport
class ZRawBuffer internal constructor(data: ZRawBufferData): ZBasicComponent<ZRawBufferData>(data) {

    @JsName("init")
    constructor(): this(ZRawBufferData())

    @JsName("initWithArgs")
    constructor(id: Int, dataArray: ByteArray): this(ZRawBufferData(id, dataArray))

    var id: Int by data::id

    var dataArray: ByteArray by data::dataArray

    val hasData: Boolean by data::hasData

}

@Serializable
@JsExport
data class ZRawBufferData(
    @ProtoNumber(1)
    var id: Int = -1,
    @ProtoNumber(2)
    var dataArray: ByteArray = byteArrayOf()
): ZComponentData() {
    val hasData: Boolean
        get() = !dataArray.isEmpty()
}

class ZRawBufferSerializer: ZComponentSerializer<ZRawBuffer, ZRawBufferData>() {
    override val deserializationStrategy: DeserializationStrategy<ZRawBufferData>
        get() = ZRawBufferData.serializer()

    override fun createComponentInstance(data: ZRawBufferData): ZRawBuffer {
        return ZRawBuffer(data)
    }
}

