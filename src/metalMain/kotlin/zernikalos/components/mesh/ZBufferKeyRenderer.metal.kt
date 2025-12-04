/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import platform.Metal.MTLStepFunctionPerVertex
import platform.Metal.MTLVertexAttributeDescriptor
import platform.Metal.MTLVertexBufferLayoutDescriptor
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger

actual class ZBufferKeyRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZBufferKeyData
) : ZComponentRenderer(ctx) {

    lateinit var attributeDescriptor: MTLVertexAttributeDescriptor
    lateinit var layoutDescriptor: MTLVertexBufferLayoutDescriptor

    actual override fun initialize() {
        attributeDescriptor = MTLVertexAttributeDescriptor()
        attributeDescriptor.offset = data.offset.toULong()
        // Use bufferId as bufferIndex so interleaved buffers share the same buffer slot
        attributeDescriptor.bufferIndex = data.bufferId.toULong()
        // In OGL we specify the base type and the size independently, not the same scenario in Metal
        val format = toMtlFormat(data.dataType)
        if (format == 0uL) {
            logger.warn("MTLVertexFormat not recognized for ${data.dataType}")
        }
        attributeDescriptor.format = format

        layoutDescriptor = MTLVertexBufferLayoutDescriptor()
        // Use the stride from data, which includes interleaved stride when buffers are interleaved
        // When stride is 0, it means tightly packed, so use element size
        val strideBytes = if (data.stride == 0) data.dataType.byteSize else data.stride
        layoutDescriptor.stride = strideBytes.toULong()
        // TODO: Check these 2 steps
        layoutDescriptor.stepRate = 1u
        layoutDescriptor.stepFunction = MTLStepFunctionPerVertex
    }

    actual override fun bind() {
    }

    actual override fun unbind() {
    }
}
