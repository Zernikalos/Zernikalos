/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponentRenderer
import zernikalos.components.ZComponentSerializer
import zernikalos.components.ZRenderizableComponent
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZBufferKeySerializer::class)
class ZBufferKey internal constructor(private val data: ZBufferKeyData): ZRenderizableComponent<ZBufferKeyRenderer>(), ZBindeable {

    /**
     * Initializes a new instance of `ZBufferKey` class.
     * It is used to instantiate a `ZBufferKey` object with default data values.
     */
    @JsName("init")
    constructor(): this(ZBufferKeyData())

    /**
     * Initializes a ZBufferKey object with the given arguments.
     *
     * @param id The ID of the buffer.
     * @param dataType The data type of the buffer.
     * @param name The name of the buffer.
     * @param size How many elements are stored per data unit.
     * @param count The number of elements of type `dataType` are stored in the buffer.
     * @param normalized Whether the data in the buffer is normalized.
     * @param offset The offset of the buffer data.
     * @param stride The stride of the buffer data.
     * @param isIndexBuffer Whether the buffer is an index buffer.
     * @param bufferId The ID of the raw buffer associated with the buffer.
     */
    @JsName("initWithArgs")
    constructor(
        id: Int,
        dataType: ZDataType,
        name: String,
        size: Int,
        count: Int,
        normalized: Boolean,
        offset: Int,
        stride: Int,
        isIndexBuffer: Boolean,
        bufferId: Int
    ): this(ZBufferKeyData(
        id,
        dataType,
        name,
        size,
        count,
        normalized,
        offset,
        stride,
        isIndexBuffer,
        bufferId
    ))

    /**
     * ID for this Buffer Key.
     */
    var id: Int by data::id

    /**
     * Type of data stored
     */
    var dataType: ZDataType by data::dataType

    var name: String by data::name

    /**
     * How many elements are stored per data unit.
     * Example: a Vec3 will have size equals to 3 in the same way a Scalar will be 1
     */
    var size: Int by data::size

    /**
     * How many elements of this type are stored.
     * Example: If we store 15 Vec3 elements in the data array the count will have a value of 15.
     */
    var count: Int by data::count

    var normalized: Boolean by data::normalized

    var offset: Int by data::offset

    /**
     * If the data is tightly represented within the array how many elements it requires to be jumped to the next one
     * Example: We store a Vec3 postion and a Vec3 normal in the very same array, the stride will be 6
     */
    var stride: Int by data::stride

    var isIndexBuffer: Boolean by data::isIndexBuffer

    /**
     * Represents the buffer ID associated with the ZBufferKey.
     */
    var bufferId: Int by data::bufferId

    override fun createRenderer(ctx: ZRenderingContext): ZBufferKeyRenderer {
        return ZBufferKeyRenderer(ctx, data)
    }

    override fun bind() = renderer.bind()

    override fun unbind() = renderer.unbind()

    override fun toString(): String {
        return "ZBufferKey(id=${data.id}, name=${data.name}, bufferId=${data.bufferId})"
    }

}

/**
 * @suppress
 */
@Serializable
@JsExport
data class ZBufferKeyData(
    @ProtoNumber(1)
    var id: Int = -1,
    @ProtoNumber(2)
    var dataType: ZDataType = ZTypes.NONE,
    @ProtoNumber(3)
    var name: String = "",
    @ProtoNumber(4)
    var size: Int = -1,
    @ProtoNumber(5)
    var count: Int = -1,
    @ProtoNumber(6)
    var normalized: Boolean = false,
    @ProtoNumber(7)
    var offset: Int = -1,
    @ProtoNumber(8)
    var stride: Int = -1,
    @ProtoNumber(9)
    var isIndexBuffer: Boolean = false,
    @ProtoNumber(10)
    var bufferId: Int = -1
)

expect class ZBufferKeyRenderer(ctx: ZRenderingContext, data: ZBufferKeyData): ZComponentRenderer {
    override fun initialize()

    override fun bind()

    override fun unbind()
}

class ZBufferKeySerializer: ZComponentSerializer<ZBufferKey, ZBufferKeyData>() {
    override val kSerializer: KSerializer<ZBufferKeyData> = ZBufferKeyData.serializer()

    override fun createComponentInstance(data: ZBufferKeyData): ZBufferKey {
        return ZBufferKey(data)
    }
}

