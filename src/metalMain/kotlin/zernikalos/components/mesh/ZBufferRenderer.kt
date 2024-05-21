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
import zernikalos.components.ZComponentRender
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext

actual class ZBufferRenderer actual constructor(ctx: ZRenderingContext, data: ZBufferData) : ZComponentRender<ZBufferData>(ctx, data) {

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
        ctx.renderEncoder?.setVertexBuffer(buffer, 0u, data.key.id.toULong())
    }

    actual override fun unbind() {
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun initialzeBuffer(ctx: ZRenderingContext, data: ZBufferData) {
        ctx as ZMtlRenderingContext

        //        val stableRef = StableRef.create(data.dataArray.toCValues())
        //        val bufferPtr = stableRef.asCPointer()
        //        buffer = ctx.device.newBufferWithBytes(bufferPtr, data.size.toULong(), 0u)

        //        val p = nativeHeap.allocPointerTo<ByteVar>()
        //
        //        p.pointed = data.dataArray.toCValues()
        //        // val bufferPtr = data.dataArray.toCValues().getPointer(ar)
        //        buffer = ctx.device.newBufferWithBytes(bufferPtr, data.size.toULong(), 0u)

        //        memScoped {
        //            val bufferPtr = data.dataArray.toCValues().getPointer(memScope)
        //            buffer = ctx.device.newBufferWithBytes(bufferPtr, data.size.toULong(), 0u)
        //        }

        // The use of pinned is to get access to a constant memory location
        data.buffer.dataArray.usePinned { pinned ->
            // Requires to set the initial position and how many bytes to copy
            // Notice that all buffers store only bytes
            // TODO: Check the options to this function
            buffer = ctx.device.newBufferWithBytes(pinned.addressOf(0), data.buffer.dataArray.size.toULong(), 1u)
        }

        buffer?.label = data.key.name
    }

    private fun initializeBufferKey(data: ZBufferData) {
        attributeDescriptor = MTLVertexAttributeDescriptor()
        attributeDescriptor.offset = data.key.offset.toULong()
        attributeDescriptor.bufferIndex = data.key.id.toULong()
        // In OGL we specify the base type and the size independently, not the same scenario in Metal
        attributeDescriptor.format = toMtlFormat(data.key.dataType)

        layoutDescriptor = MTLVertexBufferLayoutDescriptor()
        // This stride is different than OGL, in Metal all the vertex data is added into the same buffer
        // on OGL we could have different buffers at this time
        layoutDescriptor.stride = (data.key.dataType.byteSize).toULong()
        // TODO: Check these 2 steps
        layoutDescriptor.stepRate = 1u
        layoutDescriptor.stepFunction = MTLStepFunctionPerVertex
    }

}

fun toMtlFormat(dataType: ZDataType): MTLVertexFormat {
    return when (dataType) {
        ZTypes.INT -> MTLVertexFormatInt
        ZTypes.UINT -> MTLVertexFormatUInt

        ZTypes.USHORT -> MTLVertexFormatUShort
        ZTypes.SHORT -> MTLVertexFormatShort

        ZTypes.FLOAT -> MTLVertexFormatFloat

        ZTypes.VEC3F -> MTLVertexFormatFloat3
        ZTypes.VEC2F -> MTLVertexFormatFloat2
        ZTypes.VEC4F -> MTLVertexFormatFloat4

        else -> 0u
    }
}
