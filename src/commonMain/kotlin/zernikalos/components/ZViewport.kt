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
internal constructor(data: ZViewportData, renderer: ZViewportRenderer):
    ZComponent<ZViewportData, ZViewportRenderer>(data, renderer),
    ZRenderizable {

    @JsName("init")
    constructor(): this(ZViewportData(), ZViewportRenderer())


    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

    override fun render(ctx: ZRenderingContext) {
        renderer.render(ctx, data)
    }

}

@JsExport
@Serializable
data class ZViewportData(
    val clearColor: ZVector4 = ZVector4(.2f, .2f, .2f, 1.0f),
    val clearMask: Int = BufferBit.COLOR_BUFFER.value or BufferBit.DEPTH_BUFFER.value
): ZComponentData()

expect class ZViewportRenderer(): ZComponentRender<ZViewportData> {
    override fun initialize(ctx: ZRenderingContext, data: ZViewportData)

    override fun render(ctx: ZRenderingContext, data: ZViewportData)

}

class ZViewportSerializer: ZComponentSerializer<ZViewport, ZViewportData, ZViewportRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZViewportData>
        get() = ZViewportData.serializer()

    override fun createRendererComponent(): ZViewportRenderer {
        return ZViewportRenderer()
    }

    override fun createComponentInstance(data: ZViewportData, renderer: ZViewportRenderer): ZViewport {
        return ZViewport(data, renderer)
    }

}
