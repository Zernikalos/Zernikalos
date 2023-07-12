package zernikalos.components.buffer

import zernikalos.BufferUsageType
import zernikalos.GLWrap
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender
import kotlin.jvm.Transient

actual class ZBufferRenderer : ZComponentRender<ZBufferData> {
    @Transient
    lateinit var buffer: GLWrap

    actual override fun initialize(ctx: ZRenderingContext, data: ZBufferData) {
        if (!data.hasData) {
            return
        }
        
        buffer = ctx.createBuffer()
        // TODO Check errors
        //        if (!data.buffer) {
        //            throw Error("Unable to create buffer")
        //        }

        ctx.bindBuffer(data.targetBuffer, buffer)
        ctx.bufferData(data.targetBuffer, data.dataArray, BufferUsageType.STATIC_DRAW)
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZBufferData) {
        ctx.bindBuffer(data.targetBuffer, buffer)
    }

    actual override fun unbind(ctx: ZRenderingContext, data: ZBufferData) {
        super.unbind(ctx, data)
    }

}