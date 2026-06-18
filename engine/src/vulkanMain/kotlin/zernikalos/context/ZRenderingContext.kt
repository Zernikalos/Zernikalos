/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context

import zernikalos.context.gpu.ZGpuRenderPass
import zernikalos.ui.ZSurfaceView
import zernikalos.ui.ZVulkanSurfaceView

class ZVulkanRenderingContext(private val surfaceView: ZSurfaceView): ZRenderingContext {
    private var vulkanView: ZVulkanSurfaceView? = null

    override var activePass: ZGpuRenderPass? = null
        private set

    val nativeHandle: Long
        get() = vulkanView?.vulkanHandle ?: 0L

    fun getLastNativeError(): String =
        vulkanView?.getVulkanLastError().orEmpty()

    init {
        initWithSurfaceView(surfaceView)
    }

    override fun initWithSurfaceView(surfaceView: ZSurfaceView) {
        vulkanView = surfaceView as? ZVulkanSurfaceView
            ?: error("Vulkan backend requires ZernikalosVulkanView on Android")
    }

    fun renderNativeFrame() {
        vulkanView?.renderVulkanFrame()
    }

    fun createNativeBuffer(bytes: ByteArray, isIndexBuffer: Boolean): Long =
        vulkanView?.createVulkanBuffer(bytes, isIndexBuffer) ?: 0L

    fun disposeNativeBuffer(bufferHandle: Long) {
        vulkanView?.disposeVulkanBuffer(bufferHandle)
    }

    fun createNativeUniformBuffer(byteSize: Int): Long =
        vulkanView?.createVulkanUniformBuffer(byteSize) ?: 0L

    fun updateNativeUniformBuffer(uniformBufferHandle: Long, bytes: ByteArray) {
        vulkanView?.updateVulkanUniformBuffer(uniformBufferHandle, bytes)
    }

    fun disposeNativeUniformBuffer(uniformBufferHandle: Long) {
        vulkanView?.disposeVulkanUniformBuffer(uniformBufferHandle)
    }

    fun createNativePipeline(
        vertexShaderSource: String,
        fragmentShaderSource: String,
        vertexAttributes: IntArray,
        uniformBindings: IntArray,
        drawMode: Int,
    ): Long =
        vulkanView?.createVulkanPipeline(
            vertexShaderSource,
            fragmentShaderSource,
            vertexAttributes,
            uniformBindings,
            drawMode,
        ) ?: 0L

    fun disposeNativePipeline(pipelineHandle: Long) {
        vulkanView?.disposeVulkanPipeline(pipelineHandle)
    }

    fun createNativeDescriptorSet(
        pipelineHandle: Long,
        uniformBindings: IntArray,
        uniformBufferHandles: LongArray,
    ): Long =
        vulkanView?.createVulkanDescriptorSet(pipelineHandle, uniformBindings, uniformBufferHandles) ?: 0L

    fun disposeNativeDescriptorSet(descriptorSetHandle: Long) {
        vulkanView?.disposeVulkanDescriptorSet(descriptorSetHandle)
    }

    fun configureNativeRenderPass(descriptor: ZGpuRenderPassDescriptor) {
        val clear = descriptor.colorAttachments.firstOrNull()?.clearValue ?: return
        vulkanView?.setVulkanClearColor(clear.r, clear.g, clear.b, clear.a)
    }

    fun setNativePipeline(pipelineHandle: Long) {
        vulkanView?.setVulkanPipeline(pipelineHandle)
    }

    fun bindNativeDescriptorSet(descriptorSetHandle: Long) {
        vulkanView?.bindVulkanDescriptorSet(descriptorSetHandle)
    }

    fun bindNativeUniformBuffer(binding: Int, uniformBufferHandle: Long) {
        vulkanView?.bindVulkanUniformBuffer(binding, uniformBufferHandle)
    }

    fun bindNativeVertexBuffer(slot: Int, bufferHandle: Long) {
        vulkanView?.bindVulkanVertexBuffer(slot, bufferHandle)
    }

    fun bindNativeIndexBuffer(bufferHandle: Long) {
        vulkanView?.bindVulkanIndexBuffer(bufferHandle)
    }

    fun drawNativeIndexed(indexCount: Int) {
        vulkanView?.drawVulkanIndexed(indexCount)
    }

    fun createNativeMesh(positions: ByteArray, vertexCount: Int, indices: ByteArray, indexCount: Int): Long =
        vulkanView?.createVulkanMesh(positions, vertexCount, indices, indexCount) ?: 0L

    fun drawNativeMesh(meshHandle: Long) {
        vulkanView?.drawVulkanMesh(meshHandle)
    }

    fun disposeNativeMesh(meshHandle: Long) {
        vulkanView?.disposeVulkanMesh(meshHandle)
    }

    override fun <R> withActivePass(pass: ZGpuRenderPass, block: () -> R): R {
        val previous = activePass
        activePass = pass
        try {
            return block()
        } finally {
            activePass = previous
        }
    }
}
