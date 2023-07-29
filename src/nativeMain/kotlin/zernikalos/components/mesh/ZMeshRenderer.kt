package zernikalos.components.mesh

import platform.Metal.*
import zernikalos.ZMtlRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZMeshRenderer actual constructor() : ZComponentRender<ZMeshData> {

    lateinit var vertexDescriptor: MTLVertexDescriptor
    actual fun useIndexBuffer(data: ZMeshData): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun initialize(ctx: ZRenderingContext, data: ZMeshData) {

        vertexDescriptor = MTLVertexDescriptor()

        data.bufferKeys.forEach { (name, key) ->
            key.renderer.initialize(ctx, key.data)
            vertexDescriptor.attributes.setObject(key.renderer.attributeDescriptor, key.id.toULong())
            vertexDescriptor.layouts.setObject(key.renderer.layoutDescriptor, key.id.toULong())

            val buffer = data.buffers[name]
            buffer?.initialize(ctx)
        }

        data.indices?.initialize(ctx)
    }

    override fun bind(ctx: ZRenderingContext, data: ZMeshData) {
//        var index = 0
//        data.buffers.forEach { (name, buffer) ->
//            buffer.renderer.bind2(ctx, buffer.data, index)
//            index++
//        }

        val buffer = data.buffers["position"]
        buffer?.renderer?.bind2(ctx, buffer.data, 0)

        val colBuffer = data.buffers["color"]
        colBuffer?.renderer?.bind2(ctx, colBuffer.data, 1)
    }

    actual override fun render(ctx: ZRenderingContext, data: ZMeshData) {
        ctx as ZMtlRenderingContext

        val indices = data.indices!!

        ctx.renderEncoder?.drawIndexedPrimitives(
            MTLPrimitiveTypeTriangle,
            indices.count.toULong(),
            MTLIndexTypeUInt16,
            indices.renderer.buffer!!,
            0u
        )
    }

}