/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import platform.Metal.*
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext

actual class ZMeshRenderer actual constructor(ctx: ZRenderingContext, private val data: ZMeshData) : ZComponentRenderer(ctx
) {

    lateinit var vertexDescriptor: MTLVertexDescriptor

    actual override fun initialize() {

        vertexDescriptor = MTLVertexDescriptor()

        val enabledBuffers = data.buffers.values.filter { buff ->
            buff.enabled
        }

        // Initialize all buffers first
        enabledBuffers.forEach { buffer ->
            buffer.initialize(ctx)
        }

        // Configure vertex descriptor
        // For interleaved buffers, multiple attributes share the same bufferId but have different attribute ids
        val processedBufferIds = mutableSetOf<Int>()

        enabledBuffers.filter { !it.isIndexBuffer }.forEach { buffer ->
            // Set attribute descriptor using attribute id (buffer.id)
            vertexDescriptor.attributes.setObject(buffer.renderer.attributeDescriptor, buffer.id.toULong())

            // Set layout descriptor using bufferId - only once per bufferId (important for interleaved)
            val bufferId = buffer.bufferId
            if (bufferId !in processedBufferIds) {
                vertexDescriptor.layouts.setObject(buffer.renderer.layoutDescriptor, bufferId.toULong())
                processedBufferIds.add(bufferId)
            }
        }

    }

    override fun bind() {
        // Group buffers by bufferId to avoid binding the same interleaved buffer multiple times
        val processedBufferIds = mutableSetOf<Int>()

        data.buffers.values.filter {buff ->
            buff.enabled && !buff.isIndexBuffer
        }.forEach { buffer ->
            val bufferId = buffer.bufferId
            if (bufferId !in processedBufferIds) {
                buffer.bind()
                processedBufferIds.add(bufferId)
            }
        }
    }

    actual override fun render() {
        ctx as ZMtlRenderingContext

        val indices = data.indexBuffer!!

        ctx.renderEncoder?.drawIndexedPrimitives(
            convertDrawMode(data.drawMode),
            indices.count.toULong(),
            MTLIndexTypeUInt16,
            indices.renderer.buffer!!,
            0u
        )
    }

}

fun convertDrawMode(drawMode: ZDrawMode): ULong = when (drawMode) {
    ZDrawMode.POINTS -> MTLPrimitiveTypePoint
    ZDrawMode.LINES -> MTLPrimitiveTypeLine
    ZDrawMode.TRIANGLES -> MTLPrimitiveTypeTriangle
    ZDrawMode.LINE_STRIP -> MTLPrimitiveTypeLineStrip
    ZDrawMode.TRIANGLE_STRIP -> MTLPrimitiveTypeTriangleStrip
}
