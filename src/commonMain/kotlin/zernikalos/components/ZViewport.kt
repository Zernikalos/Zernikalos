package zernikalos.components

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import zernikalos.BufferBit
import zernikalos.CullModeType
import zernikalos.Enabler
import zernikalos.ZRenderingContext
import zernikalos.math.ZVector4

@Serializable(with = ZViewportSerializer::class)
class ZViewport: ZComponent<ZViewportData, ZViewportRenderer>(), ZRenderizable {

    init {
        data = ZViewportData()
        renderer = ZViewportRenderer()
    }

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

    override fun render(ctx: ZRenderingContext) {
        renderer.render(ctx, data)
    }

}

@Serializable
data class ZViewportData(
    val clearColor: ZVector4 = ZVector4(.2f, .2f, .2f, 1.0f),
    val clearMask: Int = BufferBit.COLOR_BUFFER.value or BufferBit.DEPTH_BUFFER.value
): ZComponentData()

class ZViewportRenderer: ZComponentRender<ZViewportData> {
    override fun initialize(ctx: ZRenderingContext, data: ZViewportData) {
        ctx.viewport(0, 0, 700, 700)
        ctx.enable(Enabler.DEPTH_TEST.value)
        ctx.cullFace(CullModeType.FRONT.value)
    }

    override fun render(ctx: ZRenderingContext, data: ZViewportData) {
        val v = data.clearColor
        ctx.clearColor(v.x, v.y, v.z, v.w)
        ctx.clear(data.clearMask)
    }

}

class ZViewportSerializer: ZComponentSerializer<ZViewport, ZViewportData, ZViewportRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZViewportData>
        get() = ZViewportData.serializer()

    override fun createRendererComponent(): ZViewportRenderer {
        return ZViewportRenderer()
    }

    override fun createComponentInstance(data: ZViewportData, renderer: ZViewportRenderer): ZViewport {
        return ZViewport()
    }

}
