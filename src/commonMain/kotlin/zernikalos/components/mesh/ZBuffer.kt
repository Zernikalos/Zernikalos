/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import zernikalos.ZDataType
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentRenderer
import zernikalos.components.ZRenderizableComponent
import zernikalos.components.shader.ZAttributeId
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Utility class for representing the mix of a ZBufferKey + ZBufferContent in a simpler way
 * Notice that ZBufferKey will only address one ZBufferContent, however one ZBufferContent can be addressed by more than one ZBufferKey
 */
@JsExport
class ZBuffer internal constructor(private val data: ZBufferData): ZRenderizableComponent<ZBufferRenderer>(), ZBindeable {

    /**
     * Initializes a new instance of `ZBuffer` class.
     * It is used to instantiate a `ZBuffer` object with default data values.
     */
    @JsName("init")
    constructor(): this(ZBufferData())

    /**
     * Initializes a ZBuffer object with a ZBufferKey and ZBufferContent.
     *
     * @param key The buffer key containing layout information.
     * @param content The buffer content containing the data array.
     */
    @JsName("initWithKeyAndContent")
    constructor(
        key: ZBufferKey,
        content: ZBufferContent
    ): this(ZBufferData(key, content))

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
    ): this(ZBufferData(
        ZBufferKey(id, dataType, name, size, count, normalized, offset, stride, isIndexBuffer, bufferId),
        ZBufferContent(bufferId, dataArray)
    ))

    var enabled: Boolean = false

    @JsExport.Ignore
    val attributeId: ZAttributeId
        get() = ZAttributeId.entries.find {
            id == it.id
        }!!

    /**
     * ID for this Buffer.
     */
    val id: Int by data::id

    val isIndexBuffer: Boolean by data::isIndexBuffer

    /**
     * Type of data stored
     *
     */
    val dataType: ZDataType by data::dataType

    val name: String by data::name

    /**
     * How many elements are stored per data unit.
     * Example: a Vec3 will have size equals to 3 in the same way a Scalar will be 1
     */
    val size: Int by data::size

    /**
     * How many elements of this type are stored.
     * Example: If we store 15 Vec3 elements in the data array the count will have a value of 15.
     */
    val count: Int by data::count

    val normalized: Boolean by data::normalized

    val offset: Int by data::offset

    /**
     * If the data is tightly represented within the array how many elements it requires to be jumped to the next one
     * Example: We store a Vec3 postion and a Vec3 normal in the very same array, the stride will be 6
     */
    val stride: Int by data::stride


    /**
     * Represents the buffer ID associated with the ZBuffer.
     */
    val bufferId: Int by data::bufferId

    /**
     * Represents an array of bytes for ZBufferData data.
     */
    val dataArray: ByteArray by data::dataArray

    /**
     * Indicates whether the [data] has any data.
     */
    val hasData: Boolean by data::hasData

    override fun createRenderer(ctx: ZRenderingContext): ZBufferRenderer {
        return ZBufferRenderer(ctx, data)
    }

    override fun bind() = renderer.bind()

    override fun unbind() = renderer.unbind()

    override fun toString(): String {
        return "ZBuffer(attributeId=${this.attributeId}, bufferId=${data.bufferId})"
    }

    companion object {
        /**
         * Interleaves multiple ZBuffers by combining their data arrays into a single buffer.
         * Each buffer's stride and offset are updated to reflect the interleaved layout.
         * All buffers must have the same count of elements.
         *
         * @param buffers List of ZBuffers to interleave
         * @throws IllegalArgumentException if buffers have different counts or if the list is empty
         */
        fun interleave(buffers: Array<ZBuffer>) {
            if (buffers.isEmpty()) {
                throw IllegalArgumentException("Cannot interleave an empty list of buffers")
            }

            // Verify all buffers have the same count
            val count = buffers.first().count
            if (!buffers.all { it.count == count }) {
                throw IllegalArgumentException("All buffers must have the same count for interleaving")
            }

            // Calculate the size in bytes for each element in each buffer
            val elementSizes = buffers.map { buffer ->
                buffer.dataType.byteSize
            }

            // Calculate the new stride (sum of all element sizes)
            val newStride = elementSizes.sum()

            // Create the interleaved data array
            val interleavedSize = newStride * count
            val interleavedData = ByteArray(interleavedSize)

            // Interleave the data
            var currentOffset = 0
            buffers.forEachIndexed { bufferIndex, buffer ->
                val elementSize = elementSizes[bufferIndex]
                // When stride is 0, it means tightly packed, so use elementSize
                val originalStrideBytes = if (buffer.stride == 0) elementSize else buffer.stride
                val originalOffsetBytes = buffer.offset

                // Copy data for this buffer
                for (i in 0 until count) {
                    // Calculate source position considering original stride and offset
                    val sourceStart = i * originalStrideBytes + originalOffsetBytes
                    val destStart = i * newStride + currentOffset

                    // Verify source bounds
                    if (sourceStart + elementSize > buffer.dataArray.size) {
                        throw IndexOutOfBoundsException(
                            "Buffer '${buffer.name}' source index out of bounds: " +
                            "fromIndex: $sourceStart, toIndex: ${sourceStart + elementSize}, size: ${buffer.dataArray.size}"
                        )
                    }

                    // Copy element bytes from source to interleaved array
                    buffer.dataArray.copyInto(
                        destination = interleavedData,
                        destinationOffset = destStart,
                        startIndex = sourceStart,
                        endIndex = sourceStart + elementSize
                    )
                }

                // Update buffer key with new stride and offset
                buffer.data.key.stride = newStride
                buffer.data.key.offset = currentOffset

                // Update buffer content with shared interleaved data array
                buffer.data.content.dataArray = interleavedData
                // All buffers should share the same content ID
                if (bufferIndex == 0) {
                    val sharedContentId = buffer.data.content.id
                    buffers.forEach { it.data.content.id = sharedContentId }
                }

                currentOffset += elementSize
            }
        }
    }

}

@JsExport
data class ZBufferData(
    var key: ZBufferKey = ZBufferKey(),
    var content: ZBufferContent = ZBufferContent()
): ZComponentData() {

    // Properties delegated to key
    var id: Int by key::id
    var dataType: ZDataType by key::dataType
    var name: String by key::name
    var size: Int by key::size
    var count: Int by key::count
    var normalized: Boolean by key::normalized
    var offset: Int by key::offset
    var stride: Int by key::stride
    var isIndexBuffer: Boolean by key::isIndexBuffer
    var bufferId: Int by key::bufferId

    // Properties delegated to content
    var dataArray: ByteArray by content::dataArray

    val hasData: Boolean
        get() = content.dataArray.isNotEmpty()

    // Constructor for backward compatibility
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
    ) : this(
        ZBufferKey(id, dataType, name, size, count, normalized, offset, stride, isIndexBuffer, bufferId),
        ZBufferContent(bufferId, dataArray)
    )

}

expect class ZBufferRenderer(ctx: ZRenderingContext, data: ZBufferData): ZComponentRenderer {
    override fun initialize()

    override fun bind()

    override fun unbind()
}
