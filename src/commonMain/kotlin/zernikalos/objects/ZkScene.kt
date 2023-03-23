package zernikalos.objects

import zernikalos.ZkSceneContext
import zernikalos.ZkRenderingContext
import zernikalos.components.ZkViewport
import kotlin.js.JsExport

@JsExport
class ZkScene: ZkObject() {
    var viewport: ZkViewport = ZkViewport()

    override fun internalInitialize(sceneContext: ZkSceneContext, ctx: ZkRenderingContext) {
        viewport.initialize(ctx)
    }

    override fun internalRender(sceneContext: ZkSceneContext, ctx: ZkRenderingContext) {
        viewport.render(ctx)
    }
}
