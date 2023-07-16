package zernikalos.components.buffer

import zernikalos.*
import zernikalos.components.ZComponentRender
import kotlin.jvm.Transient

actual class ZBufferRenderer : ZComponentRender<ZBufferData> {
    @Transient
    lateinit var buffer: GLWrap

    private fun getBufferTargetType(data: ZBufferData): BufferTargetType {
        return if (data.isIndexBuffer) BufferTargetType.ELEMENT_ARRAY_BUFFER else BufferTargetType.ARRAY_BUFFER
    }

    actual override fun initialize(ctx: ZRenderingContext, data: ZBufferData) {
        if (!data.hasData) {
            return
        }
        ctx as ZGLRenderingContext

        buffer = ctx.createBuffer()
        // TODO Check errors
        //        if (!data.buffer) {
        //            throw Error("Unable to create buffer")
        //        }

        val targetBufferType = getBufferTargetType(data)
        ctx.bindBuffer(targetBufferType, buffer)
        ctx.bufferData(targetBufferType, data.dataArray, BufferUsageType.STATIC_DRAW)
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZBufferData) {
        ctx as ZGLRenderingContext

        val targetBufferType = getBufferTargetType(data)
        ctx.bindBuffer(targetBufferType, buffer)
    }

    actual override fun unbind(ctx: ZRenderingContext, data: ZBufferData) {
        super.unbind(ctx, data)
    }

}