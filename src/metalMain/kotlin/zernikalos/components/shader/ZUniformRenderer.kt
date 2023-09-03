package zernikalos.components.shader

import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZUniformRenderer actual constructor() : ZComponentRender<ZUniformData> {
    actual override fun initialize(ctx: ZRenderingContext, data: ZUniformData) {
    }

    actual fun bindLocation(
        ctx: ZRenderingContext,
        data: ZUniformData,
        program: ZProgram
    ) {
    }

    actual fun bindValue(
        ctx: ZRenderingContext,
        data: ZUniformData,
        values: FloatArray
    ) {
    }

}