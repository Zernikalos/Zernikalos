package zernikalos.components.mesh

import zernikalos.*
import zernikalos.components.ZComponentRender
import kotlin.jvm.Transient

actual class ZBufferRenderer actual constructor() : ZComponentRender<ZBufferData> {

    @Transient
    lateinit var buffer: GLWrap

    fun getBufferTargetType(data: ZBufferData): BufferTargetType {
        return if (data.key.isIndexBuffer) BufferTargetType.ELEMENT_ARRAY_BUFFER else BufferTargetType.ARRAY_BUFFER
    }


    actual override fun initialize(ctx: ZRenderingContext, data: ZBufferData) {
        initializeBuffer(ctx, data)
        initializeBufferKey(ctx, data.key)
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZBufferData) {
        ctx as ZGLRenderingContext

        val bufferTargetType = getBufferTargetType(data)
        ctx.bindBuffer(bufferTargetType, buffer)
    }

    actual override fun unbind(ctx: ZRenderingContext, data: ZBufferData) {
    }

    private fun initializeBufferKey(ctx: ZRenderingContext, data: ZBufferKey) {
        ctx as ZGLRenderingContext

        val glDataType = toOglType(data.dataType)

        ctx.enableVertexAttrib(data.id)
        ctx.vertexAttribPointer(
            data.id,
            data.size,
            glDataType,
            data.normalized,
            data.stride,
            data.offset
        )
    }

    private fun initializeBuffer(ctx: ZRenderingContext, data: ZBufferData) {
        if (!data.buffer.hasData) {
            return
        }
        ctx as ZGLRenderingContext

        buffer = ctx.createBuffer()
        // TODO Check errors
        //        if (!data.buffer) {
        //            throw Error("Unable to create buffer")
        //        }

        val bufferTargetType = getBufferTargetType(data)
        ctx.bindBuffer(bufferTargetType, buffer)
        ctx.bufferData(bufferTargetType, data.buffer.dataArray, BufferUsageType.STATIC_DRAW)
    }

}