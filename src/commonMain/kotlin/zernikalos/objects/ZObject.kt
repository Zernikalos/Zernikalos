package zernikalos.objects

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZSceneContext
import zernikalos.ZRenderingContext
import zernikalos.math.ZTransform
import zernikalos.math.ZVector3F
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable
@Polymorphic
abstract class ZObject {
    @ProtoNumber(1)
    lateinit var id: String

    @ProtoNumber(2)
    lateinit var name: String

    @ProtoNumber(3)
    val transform: ZTransform = ZTransform()

    @JsName("children")
    @Transient
    var children: Array<@Polymorphic ZObject> = emptyArray()

    @Transient
    private var _parent: ZObject? = null

    val parent: ZObject?
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

    fun initialize(sceneContext: ZSceneContext, renderingContext: ZRenderingContext) {
        internalInitialize(sceneContext, renderingContext)
        children.forEach { child -> child.initialize(sceneContext, renderingContext) }
    }

    fun render(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
        internalRender(sceneContext, ctx)
        children.forEach { child -> child.render(sceneContext, ctx) }
    }

    fun addChild(child: ZObject) {
        children += child
        assignThisParent(child)
    }

    private fun assignThisParent(obj: ZObject) {
        obj._parent = this
    }

    fun lookAt(look: ZVector3F) {
        lookAt(look, ZVector3F.Up)
    }

    @JsName("lookAtWithUp")
    fun lookAt(look: ZVector3F, up: ZVector3F) {
        transform.lookAt(look, up)
    }

    fun translate(x: Float, y: Float, z: Float) {
        transform.translate(x, y, z)
    }

    protected abstract fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext)

    protected abstract fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext)

}
