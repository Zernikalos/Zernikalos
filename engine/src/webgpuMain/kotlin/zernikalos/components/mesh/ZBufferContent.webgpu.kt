/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import kotlinx.serialization.Transient
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.GPUBuffer
import zernikalos.context.webgpu.GPUBufferUsage
import zernikalos.logger.logger

actual class ZBufferContentRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZBufferContentData
) : ZComponentRenderer(ctx) {

    @Transient
    lateinit var wgpuBuffer: GPUBuffer

    private var isIndexBuffer = false

    private var attributeId: Int = -1

    val usage: Int
        get() {
            return if (isIndexBuffer)
                GPUBufferUsage.INDEX or GPUBufferUsage.COPY_DST
            else
                GPUBufferUsage.VERTEX or GPUBufferUsage.COPY_DST
        }

    /**
     * Calculates the byte size of the buffer data, aligned for WebGPU's requirements.
     *
     * WebGPU requires buffer write size to be a multiple of 4 bytes.
     * This ensures the buffer meets this alignment requirement.
     */
    private val alignedSize: Int
        get() {
            val alignment = 4
            val dataSize = data.dataArray.size
            // Round up to nearest multiple of 4: ((size + alignment - 1) / alignment) * alignment
            return ((dataSize + alignment - 1) / alignment) * alignment
        }

    /**
     * Returns the data array aligned to a multiple of 4 bytes, with padding if needed.
     */
    private val alignedData: ByteArray
        get() {
            val dataSize = data.dataArray.size
            return if (dataSize % 4 != 0) {
                data.dataArray.copyOf(alignedSize)
            } else {
                data.dataArray
            }
        }

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext
    }

    fun initializeAs(attributeId: Int, isIndexBuffer: Boolean) {
        ctx as ZWebGPURenderingContext

        this.attributeId = attributeId
        this.isIndexBuffer = isIndexBuffer

        // Each buffer will create a different GPUBuffer instance
        wgpuBuffer = ctx.device.createBuffer(
            alignedSize,
            usage,
            false,
            "Buffer${data.id}"
        )

        ctx.queue.writeBuffer(wgpuBuffer, 0, alignedData)
        logger.debug("Init Buffer content: [${data.id}] ${data.dataArray.hashCode()}")
    }

    actual override fun bind() {
        ctx as ZWebGPURenderingContext

        ctx.renderPass?.setVertexBuffer(attributeId, wgpuBuffer)
    }

    actual override fun unbind() {
    }
}
