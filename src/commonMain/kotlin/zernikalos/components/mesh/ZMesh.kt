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
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.components.*
import zernikalos.components.shader.ZAttributeId
import zernikalos.context.ZRenderingContext
import zernikalos.loader.ZLoaderContext
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Mesh will provide:
 * A relationship between the BufferKey and its RawBuffers in a more cohesive way providing just Buffers
 */
@JsExport
class ZMesh internal constructor(private val data: ZMeshData):
    ZRenderizableComponent<ZMeshRenderer>(),
    ZBindeable,
    ZRenderizable {

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

    var drawMode: ZDrawMode by data::drawMode


    @JsName("init")
    constructor(): this(ZMeshData())

    override fun createRenderer(ctx: ZRenderingContext): ZMeshRenderer {
        return ZMeshRenderer(ctx, data)
    }

    operator fun get(attrId: ZAttributeId): ZBuffer? {
        return getBufferById(attrId)
    }

    operator fun contains(attrId: ZAttributeId): Boolean {
        return hasBuffer(attrId)
    }

    val attributeIds: Set<ZAttributeId>
        get() = data.buffers.values.map { it.attributeId }.toSet()

    val position: ZBuffer?
        get() = getBufferById(ZAttributeId.POSITION)

    val normal: ZBuffer?
        get() = getBufferById(ZAttributeId.NORMAL)

    val color: ZBuffer?
        get() = getBufferById(ZAttributeId.COLOR)

    val uv: ZBuffer?
        get() = getBufferById(ZAttributeId.UV)

    val boneWeight: ZBuffer?
        get() = getBufferById(ZAttributeId.BONE_WEIGHT)

    val boneIndex: ZBuffer?
        get() = getBufferById(ZAttributeId.BONE_INDEX)

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
    @JsName("hasBufferById")
    fun hasBuffer(attrId: ZAttributeId): Boolean {
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

    override fun bind() = renderer.bind()

    override fun unbind() = renderer.unbind()

    override fun render() = renderer.render()

}

/**
 * @suppress
 */
data class ZMeshData(
    var drawMode: ZDrawMode = ZDrawMode.TRIANGLES,
    val buffers: HashMap<String, ZBuffer> = HashMap()
): ZComponentData() {

    val indexBuffer: ZBuffer?
        get() = buffers.values.find { it.isIndexBuffer }

    val hasIndexBuffer: Boolean
        get() = indexBuffer != null

    fun hasBuffer(name: String): Boolean {
        return buffers.containsKey(name)
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
data class ZBufferContent(
    @ProtoNumber(1)
    var id: Int = -1,
    @ProtoNumber(2)
    var dataArray: ByteArray = byteArrayOf()
)

@Serializable
internal data class ZRawMeshData(
    @ProtoNumber(1)
    var refId: String = "",
    @ProtoNumber(11)
    var drawMode: ZDrawMode = ZDrawMode.TRIANGLES,
    @ProtoNumber(101)
    private var bufferKeys: ArrayList<ZBufferKey> = arrayListOf(),
    @ProtoNumber(102)
    private var bufferContents: ArrayList<ZBufferContent> = arrayListOf()
) {

    @Transient
    val buffers: HashMap<String, ZBuffer> = HashMap()

    init {
        bufferKeys.forEach { key ->
            val buffer = buildBufferForKey(key)
            if (buffer != null) {
                buffers[key.name] = buffer
            }
        }
        bufferKeys.clear()
        bufferContents.clear()
    }

    private fun buildBufferForKey(key: ZBufferKey): ZBuffer? {
        val bufferContent = bufferContents.find { it.id == key.bufferId }
        if (bufferContent == null) {
            return null
        }
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
            bufferContent.dataArray
        )
    }
}

/**
 * @suppress
 */
expect class ZMeshRenderer internal constructor(ctx: ZRenderingContext, data: ZMeshData): ZComponentRenderer {

    override fun initialize()

    override fun render()
}

/**
 * @suppress
 */
internal class ZMeshSerializer(private val loaderContext: ZLoaderContext): ZComponentSerializer<ZMesh, ZRawMeshData>() {
    override val kSerializer: KSerializer<ZRawMeshData> = ZRawMeshData.serializer()

    override fun createComponentInstance(data: ZRawMeshData): ZMesh {
        if (loaderContext.hasComponent(data.refId)) {
            return loaderContext.getComponent(data.refId) as ZMesh
        }
        val meshData = ZMeshData(
            data.drawMode,
            data.buffers
        )
        val mesh = ZMesh(meshData)
        loaderContext.addComponent(data.refId, mesh)
        return mesh
    }

}
