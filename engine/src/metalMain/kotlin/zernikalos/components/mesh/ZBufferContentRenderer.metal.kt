/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Metal.MTLBufferProtocol
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger

actual class ZBufferContentRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZBufferContentData
) : ZComponentRenderer(ctx) {

    var buffer: MTLBufferProtocol? = null

    @OptIn(ExperimentalForeignApi::class)
    actual override fun initialize() {
        ctx as ZMtlRenderingContext

        // The use of pinned is to get access to a constant memory location
        data.dataArray.usePinned { pinned ->
            // Requires to set the initial position and how many bytes to copy
            // Notice that all buffers store only bytes
            // TODO: Check the options to this function
            buffer = ctx.device.newBufferWithBytes(pinned.addressOf(0), data.dataArray.size.toULong(), 1u)
            logger.debug("Init Buffer content: [${data.id}] ${pinned.addressOf(0)}")
        }

        buffer?.label = "buffer-${data.id}"
    }

    actual override fun bind() {
        ctx as ZMtlRenderingContext
        // Use bufferId as the buffer index - for interleaved buffers, this ensures all attributes
        // that share the same buffer are bound to the same buffer slot
        ctx.renderEncoder?.setVertexBuffer(buffer, 0u, data.id.toULong())
    }

    actual override fun unbind() {
    }
}
