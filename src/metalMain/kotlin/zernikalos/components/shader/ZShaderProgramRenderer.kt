package zernikalos.components.shader

import platform.Metal.MTLBufferProtocol
import platform.Metal.MTLResourceStorageModeShared
import zernikalos.ZMtlRenderingContext
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZShaderProgramRenderer actual constructor() : ZComponentRender<ZShaderProgramData> {

    var uniformBuffer: MTLBufferProtocol? = null

    actual val program: ZProgram
        get() = TODO("Not yet implemented")

    actual override fun initialize(ctx: ZRenderingContext, data: ZShaderProgramData) {
        ctx as ZMtlRenderingContext

        val uniformsSize: Int = data.uniforms.values.fold(0) { acc, zUniform -> acc + zUniform.dataType.byteSize }

        uniformBuffer = ctx.device.newBufferWithLength(uniformsSize.toULong(), MTLResourceStorageModeShared)
        uniformBuffer?.label = "UniformBuffer"
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZShaderProgramData) {
    }

    actual override fun unbind(ctx: ZRenderingContext, data: ZShaderProgramData) {
    }

}