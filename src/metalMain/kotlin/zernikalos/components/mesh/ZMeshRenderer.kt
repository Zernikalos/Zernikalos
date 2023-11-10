package zernikalos.components.mesh

import platform.Metal.*
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZMeshRenderer actual constructor(ctx: ZRenderingContext, data: ZMeshData) : ZComponentRender<ZMeshData>(ctx, data) {

    lateinit var vertexDescriptor: MTLVertexDescriptor

    actual override fun initialize() {

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

    override fun bind() {
        data.buffers.values.forEach { buffer ->
            if (!buffer.isIndexBuffer) {
                buffer.bind()
            }
        }
    }

    actual override fun render() {
        ctx as ZMtlRenderingContext

        val indices = data.indexBuffer!!

        ctx.renderEncoder?.drawIndexedPrimitives(
            MTLPrimitiveTypeTriangle,
            indices.count.toULong(),
            MTLIndexTypeUInt16,
            indices.renderer.buffer!!,
            0u
        )
    }

}