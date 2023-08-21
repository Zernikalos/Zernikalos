package zernikalos.components.mesh

import platform.Metal.*
import zernikalos.ZMtlRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZMeshRenderer actual constructor() : ZComponentRender<ZMeshData> {

    lateinit var vertexDescriptor: MTLVertexDescriptor

    actual override fun initialize(ctx: ZRenderingContext, data: ZMeshData) {

        vertexDescriptor = MTLVertexDescriptor()

        data.buffers.forEach { (name, buffer) ->
            if (buffer.id <= 1) {
                buffer.initialize(ctx)
                vertexDescriptor.attributes.setObject(buffer.renderer.attributeDescriptor, buffer.id.toULong())
                vertexDescriptor.layouts.setObject(buffer.renderer.layoutDescriptor, buffer.id.toULong())
            }
            if (buffer.isIndexBuffer) {
                buffer.initialize(ctx)
            }
        }

    }

    override fun bind(ctx: ZRenderingContext, data: ZMeshData) {
        val posBuffer = data.buffers["position"]
        posBuffer?.bind(ctx)

        val colBuffer = data.buffers["color"]
        colBuffer?.bind(ctx)
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