package zernikalos.components.mesh

import kotlinx.serialization.Serializable
import zernikalos.context.ZRenderingContext

@Serializable(with = ZMesh2Serializer::class)
actual class ZMeshRender: ZBaseMesh {

    actual constructor(): super()

    actual constructor(data: ZMeshData): super(data)

    actual override fun bind(ctx: ZRenderingContext) {
        TODO("Not yet implemented")
    }

    actual override fun unbind(ctx: ZRenderingContext) {
        TODO("Not yet implemented")
    }

    actual override fun render(ctx: ZRenderingContext) {
        TODO("Not yet implemented")
    }

}