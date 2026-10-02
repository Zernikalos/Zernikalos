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
import zernikalos.ZTypes
import zernikalos.components.*
import zernikalos.components.shader.ZAttributeId
import zernikalos.context.ZRenderingContext
import zernikalos.math.ZBox3D
import zernikalos.math.ZBoundingSphere
import zernikalos.math.ZVector3
import zernikalos.utils.toByteArray
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.sqrt

/**
 * Mesh will provide:
 * A relationship between the BufferKey and its RawBuffers in a more cohesive way providing just Buffers
 */
@JsExport
@Serializable(with = ZMeshSerializer::class)
class ZMesh internal constructor(data: ZMeshData):
    ZDataRenderComponent<ZMeshData, ZMeshRenderer>(data),
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

    /**
     * Adds a tightly packed per-vertex vec3 attribute buffer.
     * [ZBuffer.id] and [ZBuffer.bufferId] are set to [ZAttributeId.id] for the given [attr].
     */
    fun addVec3Buffer(attr: ZAttributeId, vertexCount: Int, data: FloatArray) {
        addBuffer(
            ZBuffer(
                id = attr.id,
                dataType = ZTypes.VEC3F,
                name = attr.attrName,
                size = 3,
                count = vertexCount,
                normalized = false,
                offset = 0,
                stride = 0,
                isIndexBuffer = false,
                bufferId = attr.id,
                dataArray = data.toByteArray(),
            ),
        )
    }

    /**
     * Adds a tightly packed per-vertex vec2 attribute buffer.
     * [ZBuffer.id] and [ZBuffer.bufferId] are set to [ZAttributeId.id] for the given [attr].
     */
    fun addVec2Buffer(attr: ZAttributeId, vertexCount: Int, data: FloatArray) {
        addBuffer(
            ZBuffer(
                id = attr.id,
                dataType = ZTypes.VEC2F,
                name = attr.attrName,
                size = 2,
                count = vertexCount,
                normalized = false,
                offset = 0,
                stride = 0,
                isIndexBuffer = false,
                bufferId = attr.id,
                dataArray = data.toByteArray(),
            ),
        )
    }

    /**
     * Adds a 16-bit index buffer ([ZTypes.USHORT]) using [ZAttributeId.INDICES].
     */
    fun addUShortIndexBuffer(indices: ShortArray) {
        addBuffer(
            ZBuffer(
                id = ZAttributeId.INDICES.id,
                dataType = ZTypes.USHORT,
                name = ZAttributeId.INDICES.attrName,
                size = 1,
                count = indices.size,
                normalized = false,
                offset = 0,
                stride = 0,
                isIndexBuffer = true,
                bufferId = ZAttributeId.INDICES.id,
                dataArray = indices.toByteArray(),
            ),
        )
    }

    /**
     * Computes a local-space AABB from the POSITION buffer.
     *
     * @return the enclosing box, or an empty [ZBox3D] when position data is missing,
     * empty, unsupported, out of range, or contains non-finite values
     */
    fun computeBoundingBox(): ZBox3D {
        val positions = position ?: return ZBox3D()

        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var minZ = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        var maxZ = Float.NEGATIVE_INFINITY

        val ok = positions.forEachVec3F { x, y, z ->
            if (x < minX) minX = x
            if (y < minY) minY = y
            if (z < minZ) minZ = z
            if (x > maxX) maxX = x
            if (y > maxY) maxY = y
            if (z > maxZ) maxZ = z
        }
        if (!ok) {
            return ZBox3D()
        }

        return ZBox3D(ZVector3(minX, minY, minZ), ZVector3(maxX, maxY, maxZ))
    }

    /**
     * Computes a local bounding sphere from POSITION data.
     *
     * Uses the AABB center and the maximum vertex distance to that center
     * (deterministic enclosing sphere, not a minimal one).
     *
     * @return the enclosing sphere, or an empty [ZBoundingSphere] when bounds cannot be derived
     */
    fun computeBoundingSphere(): ZBoundingSphere {
        val box = computeBoundingBox()
        if (box.isEmpty) {
            return ZBoundingSphere()
        }
        val positions = position ?: return ZBoundingSphere()
        val center = box.center

        var maxDistSq = 0f
        val ok = positions.forEachVec3F { x, y, z ->
            val dx = x - center.x
            val dy = y - center.y
            val dz = z - center.z
            val distSq = dx * dx + dy * dy + dz * dz
            if (distSq > maxDistSq) {
                maxDistSq = distSq
            }
        }
        if (!ok) {
            return ZBoundingSphere()
        }

        return ZBoundingSphere(center, sqrt(maxDistSq))
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

@Serializable
internal data class ZMeshDataDTO(
    @ProtoNumber(11)
    var drawMode: ZDrawMode = ZDrawMode.TRIANGLES,
    @ProtoNumber(101)
    private var bufferKeys: ArrayList<ZBufferKey> = arrayListOf(),
    @ProtoNumber(102)
    private var bufferContents: ArrayList<ZBufferContent> = arrayListOf()
): ZComponentData() {

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
        return ZBuffer(key, bufferContent)
    }
}

/**
 * @suppress
 */
expect class ZMeshRenderer internal constructor(ctx: ZRenderingContext, data: ZMeshData): ZComponentRenderer {

    override fun initialize()

    override fun render()

    override fun dispose()
}

/**
 * @suppress
 */
internal class ZMeshSerializer
    : ZComponentSerializerWithLoader<ZMesh, ZMeshDataDTO>() {
    override val kSerializer: KSerializer<ZMeshDataDTO> = ZMeshDataDTO.serializer()

    override fun createComponentInstance(data: ZMeshDataDTO): ZMesh {
        val meshData = ZMeshData(
            data.drawMode,
            data.buffers
        )
        return ZMesh(meshData)
    }

}
