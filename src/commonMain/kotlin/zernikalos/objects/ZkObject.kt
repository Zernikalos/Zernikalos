package zernikalos.objects

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZkSceneContext
import zernikalos.ZkRenderingContext
import zernikalos.math.ZkTransform
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable
@Polymorphic
abstract class ZkObject {
    @ProtoNumber(1)
    lateinit var id: String

    @ProtoNumber(2)
    lateinit var name: String

    @ProtoNumber(3)
    val transform: ZkTransform = ZkTransform()

    @JsName("children")
    @Transient
    var children: Array<@Polymorphic ZkObject> = emptyArray()

    @Transient
    private var _parent: ZkObject? = null

    val parent: ZkObject?
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

    fun initialize(sceneContext: ZkSceneContext, renderingContext: ZkRenderingContext) {
        internalInitialize(sceneContext, renderingContext)
        children.forEach { child -> child.initialize(sceneContext, renderingContext) }
    }

    fun render(sceneContext: ZkSceneContext, ctx: ZkRenderingContext) {
        internalRender(sceneContext, ctx)
        children.forEach { child -> child.render(sceneContext, ctx) }
    }

    fun addChild(child: ZkObject) {
        children += child
        assignThisParent(child)
    }

    private fun assignThisParent(obj: ZkObject) {
        obj._parent = this
    }

    protected abstract fun internalInitialize(sceneContext: ZkSceneContext, ctx: ZkRenderingContext)

    protected abstract fun internalRender(sceneContext: ZkSceneContext, ctx: ZkRenderingContext)

}
