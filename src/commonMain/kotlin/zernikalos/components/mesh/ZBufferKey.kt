package zernikalos.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import zernikalos.Types
import zernikalos.ZRenderingContext
import zernikalos.components.*

@Serializable(with = ZBufferKeySerializer::class)
class ZBufferKey: ZComponent<ZBufferKeyData, ZBufferKeyRenderer>() {

    override fun initialize(ctx: ZRenderingContext) {
        renderer.initialize(ctx, data)
    }

}

@Serializable
data class ZBufferKeyData(
    val id: Int,
    val dataType: Types,
    val size: Int,
    val count: Int,
    val normalized: Boolean,
    val offset: Int,
    val stride: Int
): ZComponentData()

class ZBufferKeyRenderer: ZComponentRender<ZBufferKeyData> {
    override fun initialize(ctx: ZRenderingContext, data: ZBufferKeyData) {
        ctx.enableVertexAttrib(data.id)
        ctx.vertexAttribPointer(
            data.id,
            data.size,
            data.dataType.value,
            data.normalized,
            data.stride,
            data.offset
        )
    }

}

class ZBufferKeySerializer: ZComponentSerializer<ZBufferKey, ZBufferKeyData, ZBufferKeyRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZBufferKeyData>
        get() = ZBufferKeyData.serializer()

    override fun createRendererComponent(): ZBufferKeyRenderer {
        return ZBufferKeyRenderer()
    }

    override fun createComponentInstance(data: ZBufferKeyData, renderer: ZBufferKeyRenderer): ZBufferKey {
        return ZBufferKey()
    }

}
