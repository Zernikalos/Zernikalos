package mr.robotto.objects

import mr.robotto.MrRenderingContext
import mr.robotto.MrSceneContext
import mr.robotto.components.MrViewport
import kotlin.js.JsExport

@JsExport
class MrScene: MrObject() {
    var viewport: MrViewport = MrViewport()

    override fun internalInitialize(sceneContext: MrSceneContext, ctx: MrRenderingContext) {
        viewport.initialize(ctx)
    }

    override fun internalRender(sceneContext: MrSceneContext, ctx: MrRenderingContext) {
        viewport.render(ctx)
    }
}
