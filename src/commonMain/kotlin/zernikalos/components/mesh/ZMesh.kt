/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.components.*
import zernikalos.components.shader.ZAttributeId
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Mesh will provide:
 * A relationship between the BufferKey and its RawBuffers in a more cohesive way providing just Buffers
 */
@Serializable(with = ZMeshSerializer::class)
@JsExport
class ZMesh internal constructor(data: ZMeshData): ZTemplateComponent<ZMeshData, ZMeshRenderer>(data), ZBindeable, ZRenderizable {

    /**
     * The buffers expressed in a more cohesive way providing key + buffer data in one place
     */
    val buffers: Map<String, ZBuffer> by data::buffers


    /**
     * Represents an index buffer for a ZMesh.
     *
     * @property indexBuffer The index buffer.
     */
    val indexBuffer: ZBuffer? by data::indexBuffer

    /**
     * Indicates whether the given `ZMesh` has an index buffer or not.
     *
     * @return `true` if the `ZMesh` has an index buffer, `false` otherwise.
     */
    val hasIndexBuffer: Boolean by data::hasIndexBuffer

    @JsName("init")
    constructor(): this(ZMeshData())

    override fun createRenderer(ctx: ZRenderingContext): ZBaseComponentRender {
        return ZMeshRenderer(ctx, data)
    }

    override fun bind() {
        renderer.bind()
    }

    override fun render() {
        renderer.render()
    }

    override fun unbind() {
        renderer.unbind()
    }

    /**
     * Gets the buffer by its name.
     * @param name The name of the buffer.
     * @return The requested buffer if it exists, null otherwise.
     */
    fun getBufferByName(name: String): ZBuffer? {
        return data.buffers[name]
    }

    /**
     * Gets the buffer by the specified attribute ID.
     *
     * @param attrId The attribute ID of the buffer.
     * @return The requested buffer if it exists, null otherwise.
     */
    @JsExport.Ignore
    fun getBufferById(attrId: ZAttributeId): ZBuffer? {
        return data.buffers.values.find {
            attrId == it.attributeId
        }
    }

    /**
     * Checks if a buffer with the given name exists.
     *
     * @param name The name of the buffer.
     * @return true if a buffer with the given name exists, false otherwise.
     */
    fun hasBuffer(name: String): Boolean {
        return data.hasBuffer(name)
    }

    /**
     * Checks if a buffer with the given name exists by its ID.
     *
     * @param attrId The attribute ID of the buffer.
     * @return true if a buffer with the given AttributeId exists, false otherwise.
     */
    fun hasBufferById(attrId: ZAttributeId): Boolean {
        return getBufferById(attrId) != null
    }

    /**
     * Adds a buffer to the ZMesh.
     *
     * @param buffer The buffer to be added.
     */
    fun addBuffer(buffer: ZBuffer) {
        data.buffers[buffer.name] = buffer
    }

}

/**
 * @suppress
 */
@Serializable
@JsExport
data class ZBufferKey(
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

/**
 * @suppress
 */
@Serializable
@JsExport
data class ZRawBuffer(
    @ProtoNumber(1)
    var id: Int = -1,
    @ProtoNumber(2)
    var dataArray: ByteArray = byteArrayOf()
)

/**
 * @suppress
 */
@Serializable
data class ZMeshData(
    @ProtoNumber(101)
    var bufferKeys: ArrayList<ZBufferKey> = arrayListOf(),
    @ProtoNumber(102)
    var rawBuffers: ArrayList<ZRawBuffer> = arrayListOf()
): ZComponentData() {

    @Transient
    val buffers: HashMap<String, ZBuffer> = HashMap()

    val indexBuffer: ZBuffer?
        get() = buffers.values.find { it.isIndexBuffer }

    val hasIndexBuffer: Boolean
        get() = indexBuffer != null

    init {
        bufferKeys.forEach { key ->
            val buffer = buildBufferForKey(key)
            if (buffer != null) {
                buffers[key.name] = buffer
            }
        }
    }

    fun hasBuffer(name: String): Boolean {
        return buffers.containsKey(name)
    }

    private fun buildBufferForKey(key: ZBufferKey): ZBuffer? {
        val rawBuffer = findBufferByKey(key) ?: return null
        return ZBuffer(
            key.id,
            key.dataType,
            key.name,
            key.size,
            key.count,
            key.normalized,
            key.offset,
            key.stride,
            key.isIndexBuffer,
            key.bufferId,
            rawBuffer.dataArray
        )
    }

    private fun findBufferByKey(key: ZBufferKey): ZRawBuffer? {
        return rawBuffers.find { it.id == key.bufferId }
    }
}

/**
 * @suppress
 */
expect class ZMeshRenderer(ctx: ZRenderingContext, data: ZMeshData): ZComponentRender<ZMeshData> {

    override fun initialize()

    override fun render()
}

/**
 * @suppress
 */
class ZMeshSerializer: ZComponentSerializer<ZMesh, ZMeshData>() {
    override val kSerializer: KSerializer<ZMeshData>
        get() = ZMeshData.serializer()

    override fun createComponentInstance(data: ZMeshData): ZMesh {
        return ZMesh(data)
    }

}
