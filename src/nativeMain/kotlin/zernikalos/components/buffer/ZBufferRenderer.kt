package zernikalos.components.buffer

import kotlinx.cinterop.*
import platform.Metal.MTLBufferProtocol
import platform.Metal.MTLRenderCommandEncoderProtocol
import platform.Metal.MTLResourceOptions
import zernikalos.ZMtlRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZBufferRenderer actual constructor() : ZComponentRender<ZBufferData> {

    var buffer: MTLBufferProtocol? = null

    @OptIn(ExperimentalForeignApi::class)
    actual override fun initialize(ctx: ZRenderingContext, data: ZBufferData) {
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

        data.dataArray.usePinned { pinned ->
            buffer = ctx.device.newBufferWithBytes(pinned.addressOf(0), data.dataArray.size.toULong(), 1u)
        }
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZBufferData) {
        ctx as ZMtlRenderingContext
        ctx.renderEncoder?.setVertexBuffer(buffer, 0u, 0u)
    }

    fun bind2(ctx: ZRenderingContext, data: ZBufferData, index: Int) {
        ctx as ZMtlRenderingContext
        ctx.renderEncoder?.setVertexBuffer(buffer, 0u, index.toULong())
    }

    actual override fun unbind(ctx: ZRenderingContext, data: ZBufferData) {
    }

}