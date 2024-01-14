package zernikalos.objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZSceneContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.camera.ZPerspectiveLens
import zernikalos.math.ZMatrix4
import zernikalos.math.ZVector3
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable
class ZCamera: ZObject {

    override val type = ZObjectType.CAMERA

    @ProtoNumber(4)
    var lens: ZPerspectiveLens

    val projectionMatrix: ZMatrix4
        get() = lens.projectionMatrix

    val viewMatrix: ZMatrix4
        get() = transform.matrix

    val viewProjectionMatrix: ZMatrix4
        get() = projectionMatrix * viewMatrix

    @JsName("initWithLens")
    constructor(lookAt: ZVector3, up: ZVector3, lens: ZPerspectiveLens) {
        this.transform.lookAt(lookAt, up)
        this.lens = lens
    }

    @JsName("initWithLookUp")
    constructor(lookAt: ZVector3, up: ZVector3) {
        this.transform.lookAt(lookAt, up)
        this.lens = ZPerspectiveLens.Default
    }

    @JsName("init")
    constructor() {
        this.transform.lookAt(ZVector3.Zero, ZVector3.Up)
        this.lens = ZPerspectiveLens.Default
    }

    override fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
    }

    override fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
    }

    companion object {
        val DefaultPerspectiveCamera: ZCamera
            get() = ZCamera(ZVector3.Zero, ZVector3.Up)
    }

}
