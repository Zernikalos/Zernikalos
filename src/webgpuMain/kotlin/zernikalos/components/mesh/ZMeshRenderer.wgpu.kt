package zernikalos.components.mesh

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext

actual class ZMeshRenderer actual constructor(ctx: ZRenderingContext, private val data: ZMeshData): ZComponentRenderer(ctx) {

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        data.buffers.values.filter {buff ->
            buff.enabled
        }.forEach { buff ->
            buff.initialize(ctx)
        }
    }

    override fun bind() {
        ctx as ZWebGPURenderingContext
        val position = data.buffers["position"]!!
        val indices = data.indexBuffer!!

        ctx.renderPass?.setVertexBuffer(position.id, position.renderer.wgpuBuffer)
        ctx.renderPass?.setIndexBuffer(indices.renderer.wgpuBuffer, "uint16")
    }

    actual override fun render() {
        ctx as ZWebGPURenderingContext

        val indices = data.indexBuffer!!
        ctx.renderPass?.drawIndexed(indices.count)
    }
}
