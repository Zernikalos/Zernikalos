package zernikalos.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import zernikalos.ZkSceneContext
import zernikalos.ZkRenderingContext
import kotlin.js.JsExport

@JsExport
@Serializable
@SerialName("Group")
class ZkGroup: ZkObject() {
    override fun internalInitialize(sceneContext: ZkSceneContext, ctx: ZkRenderingContext) {
    }

    override fun internalRender(sceneContext: ZkSceneContext, ctx: ZkRenderingContext) {

    }
}
