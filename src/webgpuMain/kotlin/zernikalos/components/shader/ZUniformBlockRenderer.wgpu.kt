/*
 *
 *  * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.*

actual class ZUniformBlockRenderer actual constructor(ctx: ZRenderingContext, private val data: ZUniformBlockData): ZComponentRenderer(ctx) {

    var uniformBuffer: GPUBuffer? = null

    var bindGroupLayoutEntry: GPUBindGroupLayoutEntry? = null
    var bindGroupEntry: GPUBindGroupEntry? = null

    /**
     * Calculates the byte size of the uniform data, aligned for WebGPU's requirements.
     *
     * WebGPU requires uniform buffer sizes to be a multiple of 16 bytes.
     * This ensures the buffer meets this alignment requirement.
     */
    private val alignedByteSize: Int
        get() {
            val alignment = 16
            val dataSize = data.byteSize
            // Round up to nearest multiple of 16: ((size + alignment - 1) / alignment) * alignment
            return ((dataSize + alignment - 1) / alignment) * alignment
        }

    /**
     * Returns the uniform data array aligned to a multiple of 16 bytes, with padding if needed.
     * WebGPU requires uniform buffers to be aligned to 16 bytes for proper shader access.
     */
    private val alignedData: ByteArray
        get() {
            val dataSize = data.value.byteArray.size
            return if (dataSize % 16 != 0) {
                data.value.byteArray.copyOf(alignedByteSize)
            } else {
                data.value.byteArray
            }
        }

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        uniformBuffer = ctx.device.createBuffer(
            alignedByteSize,
            GPUBufferUsage.UNIFORM or GPUBufferUsage.COPY_DST,
            false,
            "uniformBuffer-${data.uniformBlockName}"
        )

        // TODO: Review options
        bindGroupLayoutEntry = GPUBindGroupLayoutEntry(
            binding = data.id,
            visibility = GPUShaderStage.VERTEX or GPUShaderStage.FRAGMENT,
            buffer = GPUBufferBindingLayout(
                type = GPUBufferBindingType.UNIFORM
            )
        )

        bindGroupEntry = GPUBindGroupEntry(
            binding = data.id,
            resource = GPUBindGroupResource(
                buffer = uniformBuffer!!
            )
        )
    }

    actual override fun bind() {
        ctx as ZWebGPURenderingContext
        ctx.queue.writeBuffer(uniformBuffer!!, 0, alignedData)
    }

    actual override fun unbind() {
    }
}
