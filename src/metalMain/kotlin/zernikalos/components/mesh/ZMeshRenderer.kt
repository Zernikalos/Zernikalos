package zernikalos.components.mesh

import platform.Metal.*
import zernikalos.ZMtlRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZMeshRenderer actual constructor() : ZComponentRender<ZMeshData> {

    lateinit var vertexDescriptor: MTLVertexDescriptor

    actual override fun initialize(ctx: ZRenderingContext, data: ZMeshData) {

        vertexDescriptor = MTLVertexDescriptor()

        data.buffers.values.forEach { buffer ->
            // Initialization is for all buffer types (indices and vertex data)
            buffer.initialize(ctx)
            if (!buffer.isIndexBuffer) {
                // But only the vertex buffers will set the descriptors
                vertexDescriptor.attributes.setObject(buffer.renderer.attributeDescriptor, buffer.id.toULong())
                vertexDescriptor.layouts.setObject(buffer.renderer.layoutDescriptor, buffer.id.toULong())
            }
        }

    }

    override fun bind(ctx: ZRenderingContext, data: ZMeshData) {
        data.buffers.values.forEach { buffer ->
            if (!buffer.isIndexBuffer) {
                buffer.bind(ctx)
            }
        }
    }

    actual override fun render(ctx: ZRenderingContext, data: ZMeshData) {
        ctx as ZMtlRenderingContext

        val key = data.indexBufferKey!!
        val indices = data.indexBuffer!!

        ctx.renderEncoder?.drawIndexedPrimitives(
            MTLPrimitiveTypeTriangle,
            key.count.toULong(),
            MTLIndexTypeUInt16,
            indices.renderer.buffer!!,
            0u
        )
    }

}