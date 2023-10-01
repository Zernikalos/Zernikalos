package zernikalos.components.shader

import kotlinx.cinterop.*
import platform.posix.memcpy
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZUniformRenderer actual constructor() : ZComponentRender<ZUniformData> {
    actual override fun initialize(ctx: ZRenderingContext, data: ZUniformData) {
    }

    actual fun bindLocation(ctx: ZRenderingContext, data: ZUniformData, program: ZProgram) {
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun bindValue(ctx: ZRenderingContext, shaderProgram: ZShaderProgram, data: ZUniformData, values: FloatArray) {
//        data.buffer.dataArray.usePinned { pinned ->
//            buffer = ctx.device.newBufferWithBytes(pinned.addressOf(0), data.buffer.dataArray.size.toULong(), 1u)
//        }
        ctx as ZMtlRenderingContext

        //renderingContext.renderEncoder!.setVertexBuffer(model.shaderProgram.renderer.uniformBuffer, offset: 0, index: 7)
        //renderingContext.renderEncoder!.setFragmentBuffer(model.shaderProgram.renderer.uniformBuffer, offset: 0, index: 7)

        val contentPointer = shaderProgram.renderer.uniformBuffer?.contents().rawValue // + data.dataType.byteSize.toLong()
        values.usePinned { pinned ->
            //memcpy(shaderProgram.renderer.uniformBuffer?.contents(), pinned.addressOf(0), data.dataType.byteSize.toULong())

            memcpy(interpretCPointer<CPointed>(contentPointer), pinned.addressOf(0), data.dataType.byteSize.toULong())
        }

        ctx.renderEncoder?.setVertexBuffer(shaderProgram.renderer.uniformBuffer, 0u, 7u)
        ctx.renderEncoder?.setFragmentBuffer(shaderProgram.renderer.uniformBuffer, 0u, 7u)

//        values.usePinned { pinned ->
//            ctx.device.newBufferWithBytes(pinned.addressOf(0))
//        }
    }

}