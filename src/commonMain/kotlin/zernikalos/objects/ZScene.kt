package zernikalos.objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import zernikalos.context.ZSceneContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZViewport
import kotlin.js.JsExport

@JsExport
@Serializable
class ZScene: ZObject() {
    @Transient
    var viewport: ZViewport = ZViewport()

    override fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
        viewport.initialize(ctx)
    }

    override fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
        viewport.render()
    }
}
