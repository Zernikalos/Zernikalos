package zernikalos.components.shader

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import zernikalos.ZMtlRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZUniformRenderer actual constructor() : ZComponentRender<ZUniformData> {
    actual override fun initialize(ctx: ZRenderingContext, data: ZUniformData) {
    }

    actual fun bindLocation(ctx: ZRenderingContext, data: ZUniformData, program: ZProgram) {
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun bindValue(ctx: ZRenderingContext, data: ZUniformData, values: FloatArray) {
//        data.buffer.dataArray.usePinned { pinned ->
//            buffer = ctx.device.newBufferWithBytes(pinned.addressOf(0), data.buffer.dataArray.size.toULong(), 1u)
//        }
        ctx as ZMtlRenderingContext

//        values.usePinned { pinned ->
//            ctx.device.newBufferWithBytes(pinned.addressOf(0))
//        }
    }

}