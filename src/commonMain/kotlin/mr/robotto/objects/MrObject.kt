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

    fun initialize(ctx: MrRenderingContext) {
        internalInitialize(ctx)
        children.forEach { child -> child.initialize(ctx) }
    }

    fun render() {
        internalRender()
        children.forEach { child -> child.render() }
    }

    fun addChild(child: MrObject) {
        children += child
    }

    protected abstract fun internalInitialize(ctx: MrRenderingContext)

    protected abstract fun internalRender()

}
