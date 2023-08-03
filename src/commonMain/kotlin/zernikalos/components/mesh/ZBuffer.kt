package zernikalos.components.mesh

import zernikalos.ZRenderingContext
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponent
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentRender

class ZBuffer(key: ZBufferKey, buffer: ZRawBuffer):
    ZComponent<ZBufferData, ZBufferRenderer>(), ZBindeable {

    val id: Int
        get() = data.id

    val isIndexBuffer: Boolean
        get() = data.isIndexBuffer

    init {
        data = ZBufferData(key, buffer)
        renderer = ZBufferRenderer()
    }

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

    override fun bind(ctx: ZRenderingContext) {
        renderer.bind(ctx, data)
    }

    override fun unbind(ctx: ZRenderingContext) {
        renderer.unbind(ctx, data)
    }

}

data class ZBufferData(
    val key: ZBufferKey,
    val buffer: ZRawBuffer
): ZComponentData() {

    val id: Int
        get() = key.id
    val isIndexBuffer: Boolean
        get() = key.isIndexBuffer
}

expect class ZBufferRenderer(): ZComponentRender<ZBufferData> {
    override fun initialize(ctx: ZRenderingContext, data: ZBufferData)

    override fun bind(ctx: ZRenderingContext, data: ZBufferData)

    override fun unbind(ctx: ZRenderingContext, data: ZBufferData)
}