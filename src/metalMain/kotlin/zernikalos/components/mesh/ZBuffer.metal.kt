package zernikalos.components.mesh

import zernikalos.ZDataType
import zernikalos.context.ZRenderingContext

actual open class ZBufferRender : ZBaseBuffer() {
    actual constructor() {
        TODO("Not yet implemented")
    }

    actual constructor(data: ZBufferData) {
        TODO("Not yet implemented")
    }

    actual constructor(
        id: Int,
        dataType: ZDataType,
        name: String,
        size: Int,
        count: Int,
        normalized: Boolean,
        offset: Int,
        stride: Int,
        isIndexBuffer: Boolean,
        bufferId: Int,
        dataArray: ByteArray
    ) {
        TODO("Not yet implemented")
    }

    actual override fun bind(ctx: ZRenderingContext) {
    }

    actual override fun unbind(ctx: ZRenderingContext) {
    }

}