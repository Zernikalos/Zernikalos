/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

interface ZVulkanSurfaceView: ZSurfaceView {
    val vulkanHandle: Long
    fun getVulkanLastError(): String
    fun renderVulkanFrame()
    fun createVulkanBuffer(bytes: ByteArray, isIndexBuffer: Boolean): Long
    fun disposeVulkanBuffer(bufferHandle: Long)
    fun createVulkanUniformBuffer(byteSize: Int): Long
    fun updateVulkanUniformBuffer(uniformBufferHandle: Long, bytes: ByteArray)
    fun disposeVulkanUniformBuffer(uniformBufferHandle: Long)
    fun createVulkanPipeline(
        vertexShaderSource: String,
        fragmentShaderSource: String,
        vertexAttributes: IntArray,
        uniformBindings: IntArray,
        drawMode: Int,
    ): Long
    fun disposeVulkanPipeline(pipelineHandle: Long)
    fun createVulkanDescriptorSet(pipelineHandle: Long, uniformBindings: IntArray, uniformBufferHandles: LongArray): Long
    fun disposeVulkanDescriptorSet(descriptorSetHandle: Long)
    fun setVulkanClearColor(r: Float, g: Float, b: Float, a: Float)
    fun setVulkanPipeline(pipelineHandle: Long)
    fun bindVulkanDescriptorSet(descriptorSetHandle: Long)
    fun bindVulkanUniformBuffer(binding: Int, uniformBufferHandle: Long)
    fun bindVulkanVertexBuffer(slot: Int, bufferHandle: Long)
    fun bindVulkanIndexBuffer(bufferHandle: Long)
    fun drawVulkanIndexed(indexCount: Int)
    fun createVulkanMesh(positions: ByteArray, vertexCount: Int, indices: ByteArray, indexCount: Int): Long
    fun drawVulkanMesh(meshHandle: Long)
    fun disposeVulkanMesh(meshHandle: Long)
}
