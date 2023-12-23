package zernikalos.objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.skeleton.ZBone
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZSceneContext

@Serializable
class ZSkeleton: ZObject() {

    @ProtoNumber(4)
    lateinit var root: ZBone

    override fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
    }

    override fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
    }
}