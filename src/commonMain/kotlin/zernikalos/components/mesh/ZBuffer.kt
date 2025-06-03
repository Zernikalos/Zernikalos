/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.components.*
import zernikalos.components.shader.ZAttributeId
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Utility class for representing the mix of a ZBufferKey + ZRawBuffer in a simpler way
 * Notice that ZBufferKey will only address one ZRawBuffer, however one ZRawBuffer can be addressed by more than one ZBufferKey
 */
@JsExport
abstract class ZBaseBuffer: ZComponent2, ZBindeable2 {

    override val isRenderizable: Boolean = true
    /**
     * Initializes a new instance of `ZBuffer` class.
     * It is used to instantiate a `ZBuffer` object with default data values.
     */
    @JsName("init")
    constructor(): this(ZBufferData())

    @JsName("initWithData")
    constructor(data: ZBufferData):
        this(data.id,
            data.dataType,
            data.name,
            data.size,
            data.count,
            data.normalized,
            data.offset,
            data.stride,
            data.isIndexBuffer,
            data.bufferId,
            data.dataArray
        )

    /**
     * Initializes a ZBuffer object with the given arguments.
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
     * @param dataArray The array of byte data for the buffer.
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
        bufferId: Int,
        dataArray: ByteArray
    ) {
        this.id = id
        this.dataType = dataType
        this.name = name
        this.size = size
        this.count = count
        this.normalized = normalized
        this.offset = offset
        this.stride = stride
        this.isIndexBuffer = isIndexBuffer
        this.bufferId = bufferId
        this.dataArray = dataArray
    }

    var enabled: Boolean = false
    
    @JsExport.Ignore
    val attributeId: ZAttributeId
        get() = ZAttributeId.entries.find {
            id == it.id
        }!!

    /**
     * ID for this Buffer.
     */
    val id: Int

    val isIndexBuffer: Boolean

    /**
     * Type of data stored
     *
     */
    val dataType: ZDataType

    val name: String

    /**
     * How many elements are stored per data unit.
     * Example: a Vec3 will have size equals to 3 in the same way a Scalar will be 1
     */
    val size: Int

    /**
     * How many elements of this type are stored.
     * Example: If we store 15 Vec3 elements in the data array the count will have a value of 15.
     */
    val count: Int

    val normalized: Boolean

    val offset: Int

    /**
     * If the data is tightly represented within the array how many elements it requires to be jumped to the next one
     * Example: We store a Vec3 postion and a Vec3 normal in the very same array, the stride will be 6
     */
    val stride: Int


    /**
     * Represents the buffer ID associated with the ZBuffer.
     */
    val bufferId: Int

    /**
     * Represents an array of bytes for ZBufferData data.
     */
    val dataArray: ByteArray

    /**
     * Indicates whether the [data] has any data.
     */
    val hasData: Boolean
        get() = dataArray.isNotEmpty()

    override fun toString(): String {
        return "ZBuffer(attributeId=${attributeId}, bufferId=${bufferId})"
    }

}

@JsExport
data class ZBufferData(
    var id: Int = -1,
    var dataType: ZDataType = ZTypes.NONE,
    var name: String = "",
    var size: Int = -1,
    var count: Int = -1,
    var normalized: Boolean = false,
    var offset: Int = -1,
    var stride: Int = -1,
    var isIndexBuffer: Boolean = false,
    var bufferId: Int = -1,
    var dataArray: ByteArray = byteArrayOf()
): ZComponentData()

expect open class ZBufferRender: ZBaseBuffer {

    constructor()
    constructor(data: ZBufferData)
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
        bufferId: Int,
        dataArray: ByteArray
    )

    override fun bind(ctx: ZRenderingContext)
    override fun unbind(ctx: ZRenderingContext)
}

@JsExport
class ZBuffer: ZBufferRender {
    @JsName("init")
    constructor(): super()
    @JsName("initWithData")
    constructor(data: ZBufferData): super(data)
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
        bufferId: Int,
        dataArray: ByteArray
    ): super(id, dataType, name, size, count, normalized, offset, stride, isIndexBuffer, bufferId, dataArray)
}
