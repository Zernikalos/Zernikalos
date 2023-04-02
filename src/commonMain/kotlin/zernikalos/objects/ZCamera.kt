package zernikalos.objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZSceneContext
import zernikalos.ZRenderingContext
import zernikalos.components.camera.ZLens
import zernikalos.components.camera.ZPerspectiveLens
import zernikalos.math.ZMatrix4F
import zernikalos.math.ZVector3F
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable
class ZCamera: ZObject {

    @Transient
    val lookAt: ZVector3F = ZVector3F()
    @Transient
    val up: ZVector3F = ZVector3F()
    @ProtoNumber(4)
    var lens: ZPerspectiveLens

    val projectionMatrix: ZMatrix4F
        get() = lens.projectionMatrix

    val viewMatrix: ZMatrix4F
        get() = transform.matrix

    val viewProjectionMatrix: ZMatrix4F
        get() = projectionMatrix * viewMatrix

//    @JsName("lensCtor")
//    constructor(lookAt: ZVector3F, up: ZVector3F, lens: ZPerspectiveLens) {
//        this.lookAt.copy(lookAt)
//        this.up.copy(up)
//        this.lens = lens
//    }

    @JsName("defaultCtor")
    constructor(lookAt: ZVector3F, up: ZVector3F) {
        this.lens = ZPerspectiveLens.Default
    }

    override fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
    }

    override fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
    }

    companion object {
        val DefaultPerspective: ZCamera
            get() = ZCamera(ZVector3F.Zero, ZVector3F.Up)
    }

}
