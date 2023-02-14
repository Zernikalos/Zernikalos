package mr.robotto.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mr.robotto.MrRenderingContext
import mr.robotto.MrSceneContext

@Serializable
@SerialName("Group")
class MrGroup: MrObject() {
    override fun internalInitialize(sceneContext: MrSceneContext, ctx: MrRenderingContext) {
    }

    override fun internalRender(sceneContext: MrSceneContext, ctx: MrRenderingContext) {

    }
}
