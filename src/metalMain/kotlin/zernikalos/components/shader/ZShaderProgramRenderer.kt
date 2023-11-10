package zernikalos.components.shader

import platform.Metal.MTLBufferProtocol
import platform.Metal.MTLResourceStorageModeShared
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZShaderProgramRenderer actual constructor(ctx: ZRenderingContext, data: ZShaderProgramData) : ZComponentRender<ZShaderProgramData>(ctx, data) {

    var uniformBuffer: MTLBufferProtocol? = null

    actual val program: ZProgram
        get() = TODO("Not yet implemented")

    actual override fun initialize() {
        ctx as ZMtlRenderingContext

        data.uniforms.values.forEach { uniform ->
            uniform.initialize(ctx)
        }

        val uniformsSize: Int = data.uniforms.values.fold(0) { acc, zUniform -> acc + zUniform.dataType.byteSize }

        uniformBuffer = ctx.device.newBufferWithLength(uniformsSize.toULong(), MTLResourceStorageModeShared)
        uniformBuffer?.label = "UniformBuffer"
    }

    actual override fun bind() {
    }

    actual override fun unbind() {
    }

}