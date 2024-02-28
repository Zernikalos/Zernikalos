package zernikalos.components.mesh

import zernikalos.components.ZComponentRender
import zernikalos.context.*
import zernikalos.toOglBaseType
import zernikalos.logger.logger
import kotlin.jvm.Transient

actual class ZBufferRenderer actual constructor(ctx: ZRenderingContext, data: ZBufferData) : ZComponentRender<ZBufferData>(ctx, data) {

    @Transient
    lateinit var buffer: GLWrap

    private val bufferTargetType: BufferTargetType
        get() = if (data.key.isIndexBuffer) BufferTargetType.ELEMENT_ARRAY_BUFFER else BufferTargetType.ARRAY_BUFFER

    actual override fun initialize() {
        initializeBuffer(ctx, data)
        initializeBufferKey(ctx, data.key)
        logger.debug("Initializing Buffer ${data.key.name}=[@${data.id}-${bufferTargetType.name}]")
    }

    actual override fun bind() {
        ctx as ZGLRenderingContext

        ctx.bindBuffer(bufferTargetType, buffer)
    }

    actual override fun unbind() {
    }

    private fun initializeBufferKey(ctx: ZRenderingContext, data: ZBufferKey) {
        ctx as ZGLRenderingContext

        val glDataType = toOglBaseType(data.dataType)

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

        ctx.bindBuffer(bufferTargetType, buffer)
        ctx.bufferData(bufferTargetType, data.buffer.dataArray, BufferUsageType.STATIC_DRAW)
    }

}