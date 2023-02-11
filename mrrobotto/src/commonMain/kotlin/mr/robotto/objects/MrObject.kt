package mr.robotto.objects

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.MrRenderingContext
import mr.robotto.MrSceneContext
import mr.robotto.math.MrTransform
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable
@Polymorphic
abstract class MrObject {
    lateinit var name: String

    @JsName("children")
    var children: Array<@Polymorphic MrObject> = emptyArray()

    @Transient
    val transform: MrTransform = MrTransform()

    @Transient
    private var _parent: MrObject? = null

    val parent: MrObject?
        get() = _parent

    val hasParent: Boolean
        get() = _parent != null

    val isRoot: Boolean
        get() = !hasParent

    // TODO: Ugly hack for assigning parent after load from exporter
    init {
        for (child in children) {
            assignThisParent(child)
        }
    }

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
        assignThisParent(child)
    }

    private fun assignThisParent(obj: MrObject) {
        obj._parent = this
    }

    protected abstract fun internalInitialize(sceneContext: MrSceneContext, ctx: MrRenderingContext)

    protected abstract fun internalRender(sceneContext: MrSceneContext, ctx: MrRenderingContext)

}
