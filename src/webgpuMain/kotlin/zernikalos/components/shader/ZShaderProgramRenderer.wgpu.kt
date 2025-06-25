package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.GPUShaderModule
import zernikalos.logger.logger

actual class ZShaderProgramRenderer actual constructor(ctx: ZRenderingContext, private val data: ZShaderProgramData): ZComponentRenderer(ctx) {

    var shaderModule: GPUShaderModule? = null

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        logger.info("Initializing shader program...")
        shaderModule = ctx.device.createShaderModule(data.shaderSource.wgpuShaderSource)
        logger.debug(data.shaderSource.wgpuShaderSource)
    }

    actual override fun bind() {
    }

    actual override fun unbind() {
    }
}
