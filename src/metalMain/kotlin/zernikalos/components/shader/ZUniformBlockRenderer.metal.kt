/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import kotlinx.cinterop.CPointed
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.rawValue
import kotlinx.cinterop.usePinned
import platform.Metal.MTLBufferProtocol
import platform.Metal.MTLResourceStorageModeShared
import platform.posix.memcpy
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext

actual class ZUniformBlockRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZUniformBlockData
) : ZComponentRenderer(ctx) {

    var uniformBuffer: MTLBufferProtocol? = null


    actual override fun initialize() {
        ctx as ZMtlRenderingContext

        uniformBuffer = ctx.device.newBufferWithLength(data.value.byteSize.toULong(), MTLResourceStorageModeShared)
        uniformBuffer?.label = "UniformBuffer"
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override fun bind() {
        ctx as ZMtlRenderingContext

        // Memory pointer where to copy the content from uniform pinned data
        val contentPointer = uniformBuffer?.contents().rawValue
        data.value.byteArray.usePinned { pinned ->
            // Dest, Src and how many bytes
            memcpy(
                interpretCPointer<CPointed>(contentPointer),
                pinned.addressOf(0),
                data.value.byteSize.toULong()
            )
        }

        // TODO: This location is still hardcoded
        ctx.renderEncoder?.setVertexBuffer(uniformBuffer, 0u, 7u)
        ctx.renderEncoder?.setFragmentBuffer(uniformBuffer, 0u, 7u)
    }

    actual override fun unbind() {
    }

}
