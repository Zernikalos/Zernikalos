package zernikalos.components.mesh

import platform.Metal.*
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZMeshRenderer actual constructor(ctx: ZRenderingContext, data: ZMeshData) : ZComponentRender<ZMeshData>(ctx, data) {

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
            MTLPrimitiveTypeTriangle,
            indices.count.toULong(),
            MTLIndexTypeUInt16,
            indices.renderer.buffer!!,
            0u
        )
    }

}