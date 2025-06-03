package zernikalos.components.material

import zernikalos.components.ZComponent2
import zernikalos.context.ZRenderingContext

actual open class ZTextureRender : ZComponent2(), ZBindeable2 {
    actual constructor() {
        TODO("Not yet implemented")
    }

    actual constructor(
        id: String,
        width: Int,
        height: Int,
        flipX: Boolean,
        flipY: Boolean,
        dataArray: ByteArray
    ) {
        TODO("Not yet implemented")
    }

    actual override fun bind(ctx: ZRenderingContext) {
    }

    actual override fun unbind(ctx: ZRenderingContext) {
    }

}