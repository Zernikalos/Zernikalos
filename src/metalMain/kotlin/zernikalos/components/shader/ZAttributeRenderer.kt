package zernikalos.components.shader

import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZAttributeRenderer actual constructor(ctx: ZRenderingContext, data: ZAttributeData) : ZComponentRender<ZAttributeData>(ctx, data) {
    actual override fun initialize() {
    }

    actual fun bindLocation(program: ZProgram) {
    }

}