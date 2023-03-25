package zernikalos.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import zernikalos.ZSceneContext
import zernikalos.ZRenderingContext
import kotlin.js.JsExport

@JsExport
@Serializable
@SerialName("Group")
class ZGroup: ZObject() {
    override fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
    }

    override fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext) {

    }
}
