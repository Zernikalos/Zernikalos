/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import platform.Metal.MTLIndexTypeUInt16
import platform.Metal.MTLPrimitiveTypeLine
import platform.Metal.MTLPrimitiveTypeLineStrip
import platform.Metal.MTLPrimitiveTypePoint
import platform.Metal.MTLPrimitiveTypeTriangle
import platform.Metal.MTLPrimitiveTypeTriangleStrip
import platform.Metal.MTLVertexDescriptor
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger

actual class ZMeshRenderer actual constructor(ctx: ZRenderingContext, private val data: ZMeshData) : ZComponentRenderer(ctx
) {

    lateinit var vertexDescriptor: MTLVertexDescriptor

    actual override fun initialize() {

        vertexDescriptor = MTLVertexDescriptor()

        data.buffers.values.filter {buff ->
            buff.enabled
        }.forEach { buffer ->
            buffer.initialize(ctx)
            if (!buffer.isIndexBuffer) {
                vertexDescriptor.attributes.setObject(buffer.renderer.attributeDescriptor, buffer.id.toULong())
                vertexDescriptor.layouts.setObject(buffer.renderer.layoutDescriptor, buffer.id.toULong())
            }
        }

    }

    override fun bind() {
        data.buffers.values.filter {buff ->
            buff.enabled && !buff.isIndexBuffer
        }.forEach { buffer ->
            buffer.bind()
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