package zernikalos.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import zernikalos.context.ZSceneContext
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport

@JsExport
@Serializable
@SerialName("Group")
class ZGroup: ZObject() {

    override val type = ZObjectType.GROUP

    override fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
    }

    override fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext) {

    }
}
