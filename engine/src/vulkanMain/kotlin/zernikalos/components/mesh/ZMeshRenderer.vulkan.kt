/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import zernikalos.components.ZComponentRenderer
import zernikalos.components.shader.ZAttributeId
import zernikalos.components.shader.ZVulkanVertexAttributeDescriptor
import zernikalos.components.shader.vulkanLocation
import zernikalos.ZBaseType
import zernikalos.context.ZRenderingContext

actual class ZMeshRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZMeshData,
): ZComponentRenderer(ctx) {
    private val positionBuffer: ZBuffer?
        get() = data.buffers.values.find { it.attributeId == ZAttributeId.POSITION }

    val positionStride: Int
        get() = positionBuffer?.let { buffer ->
            if (buffer.stride > 0) buffer.stride else buffer.dataType.byteSize
        } ?: 0

    val positionOffset: Int
        get() = positionBuffer?.offset?.coerceAtLeast(0) ?: 0

    val positionFormat: Int
        get() = positionBuffer?.toVulkanVertexFormat() ?: VULKAN_VERTEX_FORMAT_VEC3F

    val drawMode: Int
        get() = data.drawMode.ordinal

    internal val vertexAttributeDescriptors: List<ZVulkanVertexAttributeDescriptor>
        get() = data.buffers.values
            .asSequence()
            .filter { !it.isIndexBuffer && it.enabled }
            .mapNotNull { buffer ->
                val location = buffer.attributeId.vulkanLocation()
                if (location < 0) return@mapNotNull null
                ZVulkanVertexAttributeDescriptor(
                    location = location,
                    binding = location,
                    format = buffer.toVulkanVertexFormat(),
                    offset = buffer.offset.coerceAtLeast(0),
                    stride = buffer.vertexStride,
                )
            }
            .sortedBy { it.location }
            .toList()

    actual override fun initialize() {
        data.buffers.values.forEach { it.initialize(ctx) }
    }

    override fun bind() {
        val pass = ctx.activePass ?: return
        data.buffers.values.forEach { buffer ->
            if (buffer.isIndexBuffer || !buffer.enabled) return@forEach
            val location = buffer.attributeId.vulkanLocation()
            val handle = buffer.renderer.nativeBufferHandle
            if (location >= 0 && handle != 0L) {
                pass.setVertexBuffer(location, handle)
            }
        }

        val indexHandle = data.indexBuffer?.renderer?.nativeBufferHandle ?: 0L
        if (indexHandle != 0L) {
            pass.setIndexBuffer(indexHandle)
        }
    }

    actual override fun render() {
        val pass = ctx.activePass ?: return
        if (data.hasIndexBuffer) {
            pass.drawIndexed(data.indexBuffer?.count ?: 0)
        } else {
            pass.draw(positionBuffer?.count ?: 0)
        }
    }

    actual override fun dispose() {
        data.buffers.values.forEach { it.dispose() }
    }

}

private val ZBuffer.vertexStride: Int
    get() = if (stride > 0) stride else dataType.byteSize

private const val VULKAN_VERTEX_FORMAT_FLOAT = 1
private const val VULKAN_VERTEX_FORMAT_VEC2F = 2
private const val VULKAN_VERTEX_FORMAT_VEC3F = 3
private const val VULKAN_VERTEX_FORMAT_VEC4F = 4
private const val VULKAN_VERTEX_FORMAT_UINT = 5
private const val VULKAN_VERTEX_FORMAT_VEC2U = 6
private const val VULKAN_VERTEX_FORMAT_VEC3U = 7
private const val VULKAN_VERTEX_FORMAT_VEC4U = 8

private fun ZBuffer.toVulkanVertexFormat(): Int {
    return when (dataType.type) {
        ZBaseType.FLOAT -> when (dataType.size) {
            1 -> VULKAN_VERTEX_FORMAT_FLOAT
            2 -> VULKAN_VERTEX_FORMAT_VEC2F
            3 -> VULKAN_VERTEX_FORMAT_VEC3F
            4 -> VULKAN_VERTEX_FORMAT_VEC4F
            else -> VULKAN_VERTEX_FORMAT_VEC3F
        }
        ZBaseType.UNSIGNED_INT -> when (dataType.size) {
            1 -> VULKAN_VERTEX_FORMAT_UINT
            2 -> VULKAN_VERTEX_FORMAT_VEC2U
            3 -> VULKAN_VERTEX_FORMAT_VEC3U
            4 -> VULKAN_VERTEX_FORMAT_VEC4U
            else -> VULKAN_VERTEX_FORMAT_VEC4U
        }
        else -> VULKAN_VERTEX_FORMAT_VEC3F
    }
}
