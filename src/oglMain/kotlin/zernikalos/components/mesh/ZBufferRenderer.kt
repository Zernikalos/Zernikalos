package zernikalos.components.mesh

import zernikalos.*
import zernikalos.components.ZComponentRender
import kotlin.jvm.Transient

actual class ZBufferRenderer : ZComponentRender<ZBufferData> {
    @Transient
    lateinit var buffer: GLWrap
    private var isIndexBuffer = false

    private val bufferTargetType: BufferTargetType
        get() = if (isIndexBuffer) BufferTargetType.ELEMENT_ARRAY_BUFFER else BufferTargetType.ARRAY_BUFFER

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

        ctx.bindBuffer(bufferTargetType, buffer)
        ctx.bufferData(bufferTargetType, data.dataArray, BufferUsageType.STATIC_DRAW)
    }


    actual fun initializeIndexBuffer(ctx: ZRenderingContext, data: ZBufferData) {
        isIndexBuffer = true
        initialize(ctx, data)
    }

    actual fun initializeVertexBuffer(ctx: ZRenderingContext, data: ZBufferData) {
        isIndexBuffer = false
        initialize(ctx, data)
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZBufferData) {
        ctx as ZGLRenderingContext

        ctx.bindBuffer(bufferTargetType, buffer)
    }

    actual override fun unbind(ctx: ZRenderingContext, data: ZBufferData) {
        super.unbind(ctx, data)
    }

}