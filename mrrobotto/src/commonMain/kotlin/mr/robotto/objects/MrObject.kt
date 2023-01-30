package mr.robotto.objects

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import mr.robotto.MrRenderingContext
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@ExperimentalJsExport
@Serializable
@Polymorphic
abstract class MrObject {
    lateinit var name: String

    @JsName("children")
    var children: Array<@Polymorphic MrObject> = emptyArray()

    fun initialize(sceneContext: MrSceneContext, renderingContext: MrRenderingContext) {
        internalInitialize(sceneContext, renderingContext)
        children.forEach { child -> child.initialize(sceneContext, renderingContext) }
    }

    fun render(sceneContext: MrSceneContext, ctx: MrRenderingContext) {
        internalRender(sceneContext, ctx)
        children.forEach { child -> child.render(sceneContext, ctx) }
    }

    fun addChild(child: MrObject) {
        children += child
    }

    protected abstract fun internalInitialize(sceneContext: MrSceneContext, ctx: MrRenderingContext)

    protected abstract fun internalRender(sceneContext: MrSceneContext, ctx: MrRenderingContext)

}
