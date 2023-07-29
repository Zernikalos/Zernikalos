package zernikalos.components.shader

import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZShaderProgramRenderer actual constructor() : ZComponentRender<ZShaderProgramData> {
    actual val program: ZProgram
        get() = TODO("Not yet implemented")

    actual override fun initialize(
        ctx: ZRenderingContext,
        data: ZShaderProgramData
    ) {
    }

    actual override fun bind(ctx: ZRenderingContext, data: ZShaderProgramData) {
    }

    actual override fun unbind(
        ctx: ZRenderingContext,
        data: ZShaderProgramData
    ) {
    }

}