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
import zernikalos.components.*
import zernikalos.components.shader.ZAttributeId
import zernikalos.context.ZRenderingContext
import zernikalos.logger.ZLoggable
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Mesh will provide:
 * A relationship between the BufferKey and its RawBuffers in a more cohesive way providing just Buffers
 */
@Serializable(with = ZMeshSerializer::class)
@JsExport
class ZMesh internal constructor(data: ZMeshData): ZComponent<ZMeshData, ZMeshRenderer>(data), ZBindeable, ZRenderizable, ZRef, ZLoggable {

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

    override var refId: Int
        get() = computeRefIdFromString(data.toString())
        set(value) {}

    @JsName("init")
    constructor(): this(ZMeshData())

    override fun createRenderer(ctx: ZRenderingContext): ZMeshRenderer {
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
     * Adds a buffer key to the mesh data.
     * @param bufferKey The key of the buffer to add.
     */
    fun addBufferKey(bufferKey: ZBufferKey) {
        data.addBufferKey(bufferKey)
    }

    /**
     * Gets the buffer by its name.
     * @param name The name of the buffer.
     * @return The requested buffer if it exists, null otherwise.
     */
    fun getBufferByName(name: String): ZBuffer? {
        return data.buffers[name]
    }

    @JsExport.Ignore
    fun getBufferById(attrId: ZAttributeId): ZBuffer? {
        return data.buffers.values.find {
            attrId == it.attributeId
        }
    }

    fun hasBufferKey(name: String): Boolean {
        return data.hasBufferKey(name)
    }

    fun getBufferKey(name: String): ZBufferKey? {
        return data.getBufferKey(name)
    }

    /**
     * Adds a raw buffer to the mesh data.
     * @param rawBuffer The raw buffer to add.
     */
    fun addRawBuffer(rawBuffer: ZRawBuffer) {
        data.addRawBuffer(rawBuffer)
    }

    /**
     * Builds or prepares all the buffers for rendering.
     * This most likely involves transferring data to GPU memory.
     */
    fun buildBuffers() {
        data.buildBuffers()
    }

}

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

    fun addBufferKey(bufferKey: ZBufferKey) {
        bufferKeys.add(bufferKey)
    }

    fun getBufferKey(name: String): ZBufferKey? {
        return bufferKeys.find { it.name == name }
    }

    fun hasBufferKey(name: String): Boolean {
        return getBufferKey(name) != null
    }

    fun addRawBuffer(rawBuffer: ZRawBuffer) {
        rawBuffers.add(rawBuffer)
    }

    internal fun buildBuffers() {
        bufferKeys.forEach { key ->
            val buffer = buildBufferForKey(key)
            if (buffer != null) {
                buffers[key.name] = buffer
            }
        }
    }

    private fun buildBufferForKey(key: ZBufferKey): ZBuffer? {
        val rawBuffer = findBufferByKey(key) ?: return null
        return ZBuffer(key, rawBuffer)
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
