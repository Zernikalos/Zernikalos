/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context.gpu

import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.GPUBindGroup
import zernikalos.context.webgpu.GPUBuffer
import zernikalos.context.webgpu.GPURenderPassEncoder
import zernikalos.context.webgpu.GPURenderPipeline

actual class ZGpuRenderPass internal constructor(
    private val encoder: GPURenderPassEncoder,
    private val renderingContext: ZWebGPURenderingContext,
) {
    actual fun applyPassState(state: ZGpuPassState) {
        // Pass state is encoded in pipeline descriptors and render-pass descriptors on WebGPU.
    }

    actual fun end() {
        encoder.end()
        renderingContext.clearActivePass()
    }

    fun setPipeline(pipeline: GPURenderPipeline) {
        encoder.setPipeline(pipeline)
    }

    fun setVertexBuffer(slot: Int, buffer: GPUBuffer) {
        encoder.setVertexBuffer(slot, buffer)
    }

    fun setIndexBuffer(buffer: GPUBuffer, format: String) {
        encoder.setIndexBuffer(buffer, format)
    }

    fun drawIndexed(indexCount: Int) {
        encoder.drawIndexed(indexCount)
    }

    fun setBindGroup(index: Int, group: GPUBindGroup) {
        encoder.setBindGroup(index, group)
    }
}
