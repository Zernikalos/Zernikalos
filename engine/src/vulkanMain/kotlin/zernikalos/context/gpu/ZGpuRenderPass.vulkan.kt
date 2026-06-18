/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context.gpu

import zernikalos.context.ZVulkanRenderingContext

actual class ZGpuRenderPass internal constructor(
    private val ctx: ZVulkanRenderingContext,
) {
    actual fun applyPassState(state: ZGpuPassState) {
    }

    fun draw(vertexCount: Int) {
    }

    fun drawIndexed(indexCount: Int) {
        ctx.drawNativeIndexed(indexCount)
    }

    fun setPipeline(pipelineHandle: Long) {
        ctx.setNativePipeline(pipelineHandle)
    }

    fun setDescriptorSet(descriptorSetHandle: Long) {
        ctx.bindNativeDescriptorSet(descriptorSetHandle)
    }

    fun setUniformBuffer(binding: Int, uniformBufferHandle: Long) {
        ctx.bindNativeUniformBuffer(binding, uniformBufferHandle)
    }

    fun setVertexBuffer(slot: Int, bufferHandle: Long) {
        ctx.bindNativeVertexBuffer(slot, bufferHandle)
    }

    fun setIndexBuffer(bufferHandle: Long) {
        ctx.bindNativeIndexBuffer(bufferHandle)
    }

    fun drawMesh(meshHandle: Long) {
        ctx.drawNativeMesh(meshHandle)
    }

    actual fun end() {
    }
}
