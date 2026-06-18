/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import zernikalos.context.ZRenderingContext
import zernikalos.context.ZVulkanRenderingContext

actual class ZModelRenderer actual constructor(
    private val ctx: ZRenderingContext,
    private val model: ZModel,
) {
    private var nativePipelineHandle: Long = 0L
    private var nativeDescriptorSetHandle: Long = 0L

    private val vulkanContext: ZVulkanRenderingContext?
        get() = ctx as? ZVulkanRenderingContext

    actual fun initialize() {
        val vertexAttributes = model.mesh.renderer.vertexAttributeDescriptors
        if (vertexAttributes.isEmpty()) return

        val uniformBindings = model.shaderProgram.uniforms.blocks.values.map { it.id }.toIntArray()
        nativePipelineHandle = vulkanContext?.createNativePipeline(
            vertexShaderSource = model.shaderProgram.shaderSource.vulkanVertexShaderSource,
            fragmentShaderSource = model.shaderProgram.shaderSource.vulkanFragmentShaderSource,
            vertexAttributes = vertexAttributes.flatMap {
                listOf(it.location, it.binding, it.format, it.offset, it.stride)
            }.toIntArray(),
            uniformBindings = uniformBindings,
            drawMode = model.mesh.renderer.drawMode,
        ) ?: 0L

        if (nativePipelineHandle == 0L) {
            error("Failed to create Vulkan pipeline: ${vulkanContext?.getLastNativeError().orEmpty()}")
        }

        val uniformBufferHandles = model.shaderProgram.uniforms.blocks.values
            .map { it.renderer.nativeUniformBufferHandle }
            .toLongArray()

        nativeDescriptorSetHandle = vulkanContext?.createNativeDescriptorSet(
            nativePipelineHandle,
            uniformBindings,
            uniformBufferHandles,
        ) ?: 0L

        if (uniformBindings.isNotEmpty() && nativeDescriptorSetHandle == 0L) {
            error("Failed to create Vulkan descriptor set: ${vulkanContext?.getLastNativeError().orEmpty()}")
        }
    }

    actual fun render() {
        val pass = ctx.activePass ?: return
        if (nativePipelineHandle == 0L) return

        pass.setPipeline(nativePipelineHandle)
        if (nativeDescriptorSetHandle != 0L) {
            model.shaderProgram.uniforms.blocks.values.forEach { it.renderer.update() }
            pass.setDescriptorSet(nativeDescriptorSetHandle)
        }
        model.shaderProgram.bind()
        model.mesh.bind()
        model.mesh.render()
        model.mesh.unbind()
        model.shaderProgram.unbind()
    }

    actual fun dispose() {
        if (nativeDescriptorSetHandle != 0L) {
            vulkanContext?.disposeNativeDescriptorSet(nativeDescriptorSetHandle)
            nativeDescriptorSetHandle = 0L
        }
        if (nativePipelineHandle != 0L) {
            vulkanContext?.disposeNativePipeline(nativePipelineHandle)
            nativePipelineHandle = 0L
        }
    }
}
