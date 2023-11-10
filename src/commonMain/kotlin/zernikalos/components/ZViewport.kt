package zernikalos.components

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import zernikalos.context.BufferBit
import zernikalos.context.ZRenderingContext
import zernikalos.math.ZVector4
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZViewportSerializer::class)
class ZViewport
internal constructor(data: ZViewportData):
    ZComponent<ZViewportData, ZViewportRenderer>(data),
    ZRenderizable {

    @JsName("init")
    constructor(): this(ZViewportData())


    override fun internalInitialize(ctx: ZRenderingContext) {
        renderer.initialize()
    }

    override fun createRenderer(ctx: ZRenderingContext): ZViewportRenderer {
        return ZViewportRenderer(ctx, data)
    }

    override fun render() {
        renderer.render()
    }

}

@JsExport
@Serializable
data class ZViewportData(
    val clearColor: ZVector4 = ZVector4(.2f, .2f, .2f, 1.0f),
    val clearMask: Int = BufferBit.COLOR_BUFFER.value or BufferBit.DEPTH_BUFFER.value
): ZComponentData()

expect class ZViewportRenderer(ctx: ZRenderingContext, data: ZViewportData): ZComponentRender<ZViewportData> {
    override fun initialize()

    override fun render()

}

class ZViewportSerializer: ZComponentSerializer<ZViewport, ZViewportData, ZViewportRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZViewportData>
        get() = ZViewportData.serializer()

    override fun createComponentInstance(data: ZViewportData): ZViewport {
        return ZViewport(data)
    }

}
