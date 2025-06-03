package zernikalos.components.mesh

import zernikalos.ZDataType
import zernikalos.context.*
import zernikalos.logger.logger
import zernikalos.toOglBaseType

actual open class ZBufferRender : ZBaseBuffer {
    actual constructor(): super()

    actual constructor(data: ZBufferData): super(data)

    actual constructor(
        id: Int,
        dataType: ZDataType,
        name: String,
        size: Int,
        count: Int,
        normalized: Boolean,
        offset: Int,
        stride: Int,
        isIndexBuffer: Boolean,
        bufferId: Int,
        dataArray: ByteArray
    ): super(id, dataType, name, size, count, normalized, offset, stride, isIndexBuffer, bufferId, dataArray)

    lateinit var buffer: GLWrap

    private val bufferTargetType: BufferTargetType
        get() = if (isIndexBuffer) BufferTargetType.ELEMENT_ARRAY_BUFFER else BufferTargetType.ARRAY_BUFFER

    override fun internalRenderInitialize(ctx: ZRenderingContext) {
        initializeBuffer(ctx)
        initializeBufferKey(ctx)
        logger.debug("Initializing Buffer ${name}=[@${id}-${bufferTargetType.name}]")
    }

    actual override fun bind(ctx: ZRenderingContext) {
        ctx as ZGLRenderingContext

        ctx.bindBuffer(bufferTargetType, buffer)
    }

    actual override fun unbind(ctx: ZRenderingContext) {
    }

    private fun initializeBufferKey(ctx: ZRenderingContext) {
        ctx as ZGLRenderingContext

        val glDataType = toOglBaseType(dataType)

        ctx.enableVertexAttrib(id)
        ctx.vertexAttribPointer(
            id,
            size,
            glDataType,
            normalized,
            stride,
            offset
        )
    }

    private fun initializeBuffer(ctx: ZRenderingContext) {
        if (!hasData) {
            return
        }
        ctx as ZGLRenderingContext

        buffer = ctx.createBuffer()
        // TODO Check errors
        //        if (!data.buffer) {
        //            throw Error("Unable to create buffer")
        //        }

        ctx.bindBuffer(bufferTargetType, buffer)
        ctx.bufferData(bufferTargetType, dataArray, BufferUsageType.STATIC_DRAW)
    }

}