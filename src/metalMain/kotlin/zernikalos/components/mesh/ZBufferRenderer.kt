package zernikalos.components.mesh

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Metal.*
import zernikalos.ZMtlRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZBufferRenderer actual constructor() : ZComponentRender<ZBufferData> {

    var buffer: MTLBufferProtocol? = null
    lateinit var attributeDescriptor: MTLVertexAttributeDescriptor
    lateinit var layoutDescriptor: MTLVertexBufferLayoutDescriptor

    actual override fun initialize(ctx: ZRenderingContext, data: ZBufferData) {
        if (data.isIndexBuffer) {
            initialzeBuffer(ctx, data)
        } else {
            initializeBufferKey(ctx, data)
            initialzeBuffer(ctx, data)
        }
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZBufferData) {
        ctx as ZMtlRenderingContext
        ctx.renderEncoder?.setVertexBuffer(buffer, 0u, data.key.id.toULong())
    }

    actual override fun unbind(ctx: ZRenderingContext, data: ZBufferData) {
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun initialzeBuffer(ctx: ZRenderingContext, data: ZBufferData) {
        ctx as ZMtlRenderingContext

        //        val stableRef = StableRef.create(data.dataArray.toCValues())
        //        val bufferPtr = stableRef.asCPointer()
        //        buffer = ctx.device.newBufferWithBytes(bufferPtr, data.size.toULong(), 0u)

        //        val p = nativeHeap.allocPointerTo<ByteVar>()
        //
        //        p.pointed = data.dataArray.toCValues()
        //        // val bufferPtr = data.dataArray.toCValues().getPointer(ar)
        //        buffer = ctx.device.newBufferWithBytes(bufferPtr, data.size.toULong(), 0u)

        //        memScoped {
        //            val bufferPtr = data.dataArray.toCValues().getPointer(memScope)
        //            buffer = ctx.device.newBufferWithBytes(bufferPtr, data.size.toULong(), 0u)
        //        }

        data.buffer.dataArray.usePinned { pinned ->
            buffer = ctx.device.newBufferWithBytes(pinned.addressOf(0), data.buffer.dataArray.size.toULong(), 1u)
        }
    }

    private fun initializeBufferKey(ctx: ZRenderingContext, data: ZBufferData) {
        attributeDescriptor = MTLVertexAttributeDescriptor()
        attributeDescriptor.offset = data.key.offset.toULong()
        attributeDescriptor.bufferIndex = data.key.id.toULong()
        attributeDescriptor.format = MTLVertexFormatFloat3

        layoutDescriptor = MTLVertexBufferLayoutDescriptor()
        layoutDescriptor.stride = 12u
        layoutDescriptor.stepRate = 1u
        layoutDescriptor.stepFunction = MTLStepFunctionPerVertex
    }

}