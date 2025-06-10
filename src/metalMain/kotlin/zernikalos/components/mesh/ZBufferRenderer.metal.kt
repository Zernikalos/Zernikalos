/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Metal.*
import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger

actual class ZBufferRenderer actual constructor(ctx: ZRenderingContext, private val data: ZBufferData) : ZComponentRenderer(ctx) {

    var buffer: MTLBufferProtocol? = null
    lateinit var attributeDescriptor: MTLVertexAttributeDescriptor
    lateinit var layoutDescriptor: MTLVertexBufferLayoutDescriptor

    actual override fun initialize() {
        if (data.isIndexBuffer) {
            initialzeBuffer(ctx, data)
        } else {
            initializeBufferKey(data)
            initialzeBuffer(ctx, data)
        }
    }

    actual override fun bind() {
        ctx as ZMtlRenderingContext
        ctx.renderEncoder?.setVertexBuffer(buffer, 0u, data.id.toULong())
    }

    actual override fun unbind() {
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun initialzeBuffer(ctx: ZRenderingContext, data: ZBufferData) {
        ctx as ZMtlRenderingContext

        // The use of pinned is to get access to a constant memory location
        data.dataArray.usePinned { pinned ->
            // Requires to set the initial position and how many bytes to copy
            // Notice that all buffers store only bytes
            // TODO: Check the options to this function
            buffer = ctx.device.newBufferWithBytes(pinned.addressOf(0), data.dataArray.size.toULong(), 1u)
        }

        buffer?.label = data.name
    }

    private fun initializeBufferKey(data: ZBufferData) {
        attributeDescriptor = MTLVertexAttributeDescriptor()
        attributeDescriptor.offset = data.offset.toULong()
        attributeDescriptor.bufferIndex = data.id.toULong()
        // In OGL we specify the base type and the size independently, not the same scenario in Metal
        val format = toMtlFormat(data.dataType)
        if (format == 0uL) {
            logger.warn("MTLVertexFormat not recognized for ${data.dataType}")
        }
        attributeDescriptor.format = format

        layoutDescriptor = MTLVertexBufferLayoutDescriptor()
        // This stride is different than OGL, in Metal all the vertex data is added into the same buffer
        // on OGL we could have different buffers at this time
        layoutDescriptor.stride = (data.dataType.byteSize).toULong()
        // TODO: Check these 2 steps
        layoutDescriptor.stepRate = 1u
        layoutDescriptor.stepFunction = MTLStepFunctionPerVertex
    }

}

fun toMtlFormat(dataType: ZDataType): MTLVertexFormat {
    return when (dataType) {
        ZTypes.BYTE -> MTLVertexFormatChar
        ZTypes.BYTE2 -> MTLVertexFormatChar2
        ZTypes.BYTE3 -> MTLVertexFormatChar3
        ZTypes.BYTE4 -> MTLVertexFormatChar4

        ZTypes.UBYTE -> MTLVertexFormatUChar
        ZTypes.UBYTE2 -> MTLVertexFormatUChar2
        ZTypes.UBYTE3 -> MTLVertexFormatUChar3
        ZTypes.UBYTE4 -> MTLVertexFormatUChar4

        ZTypes.INT -> MTLVertexFormatInt
        ZTypes.INT2 -> MTLVertexFormatInt2
        ZTypes.INT3 -> MTLVertexFormatInt3
        ZTypes.INT4 -> MTLVertexFormatInt4

        ZTypes.UINT -> MTLVertexFormatUInt
        ZTypes.UINT2 -> MTLVertexFormatUInt2
        ZTypes.UINT3 -> MTLVertexFormatUInt3
        ZTypes.UINT4 -> MTLVertexFormatUInt4

        ZTypes.USHORT -> MTLVertexFormatUShort
        ZTypes.USHORT2 -> MTLVertexFormatUShort2
        ZTypes.USHORT3 -> MTLVertexFormatUShort3
        ZTypes.USHORT4 -> MTLVertexFormatUShort4

        ZTypes.SHORT -> MTLVertexFormatShort
        ZTypes.SHORT2 -> MTLVertexFormatShort2
        ZTypes.SHORT3 -> MTLVertexFormatShort3
        ZTypes.SHORT4 -> MTLVertexFormatShort4

        ZTypes.FLOAT -> MTLVertexFormatFloat
        ZTypes.VEC2F -> MTLVertexFormatFloat2
        ZTypes.VEC3F -> MTLVertexFormatFloat3
        ZTypes.VEC4F -> MTLVertexFormatFloat4

        else -> 0u
    }
}
