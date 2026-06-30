/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import kotlinx.cinterop.*
import platform.Metal.MTLBufferProtocol
import platform.Metal.MTLResourceStorageModeShared
import platform.posix.memcpy
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.shader.ZShaderType
import zernikalos.logger.logger

actual class ZUniformRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZUniformBlockData
) : ZComponentRenderer(ctx) {

    var uniformBuffer: MTLBufferProtocol? = null

    /**
     * Calculates the byte size of the uniform data, aligned for Metal's requirements.
     *
     * Metal requires that the size of a buffer bound to a shader is a multiple of a certain value,
     * typically 16 bytes for structures containing vector types like `float4`. This property ensures
     * that the buffer allocated for this uniform block has a size that meets this alignment
     * requirement by rounding up the actual data size.
     */
    private val alignedByteSize: Long
        get() {
            // The alignment requirement is typically 16 bytes for Metal.
            val alignment = 16L
            return (data.byteSize + alignment - 1) / alignment * alignment
        }

    actual override fun initialize() {
        ctx as ZMtlRenderingContext

        uniformBuffer = ctx.device.newBufferWithLength(alignedByteSize.toULong(), MTLResourceStorageModeShared)
        uniformBuffer?.label = "UniformBuffer-${data.uniformBlockName}"
        logger.debug("Initializing uniform: [(ID ${data.id})${data.uniformBlockName}] ($alignedByteSize)")
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun bind() {
        ctx as ZMtlRenderingContext
        val pass = ctx.activePass ?: return
        val buffer = uniformBuffer ?: return

        // Memory pointer where to copy the content from uniform pinned data
        val contentPointer = uniformBuffer?.contents().rawValue
        data.value.byteArray.usePinned { pinned ->
            logger.debugOnce("Binding uniform [(ID ${data.id})${data.uniformBlockName}] (${data.value.byteSize}/$alignedByteSize)")
            // Dest, Src and how many bytes
            memcpy(
                interpretCPointer<CPointed>(contentPointer),
                pinned.addressOf(0),
                data.value.byteSize.toULong()
            )
        }

        pass.setUniformBuffer(ZShaderType.VERTEX_SHADER, data.id, buffer, 0L)
        pass.setUniformBuffer(ZShaderType.FRAGMENT_SHADER, data.id, buffer, 0L)
    }

    override fun unbind() {
    }

    actual override fun dispose() {
        uniformBuffer = null
    }

}
