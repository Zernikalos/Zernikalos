package zernikalos.objects

import zernikalos.ZSceneContext
import zernikalos.ZRenderingContext
import zernikalos.components.camera.ZLens
import zernikalos.components.camera.ZPerspectiveLens
import zernikalos.math.ZMatrix4F
import zernikalos.math.ZVector3F
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ZCamera: ZObject {

    val lookAt: ZVector3F = ZVector3F()
    val up: ZVector3F = ZVector3F()
    var lens: ZLens

    val projectionMatrix: ZMatrix4F
        get() = lens.projectionMatrix

    val viewMatrix: ZMatrix4F
        get() = transform.matrix

    val viewProjectionMatrix: ZMatrix4F
        get() = projectionMatrix * viewMatrix

    @JsName("lensCtor")
    constructor(lookAt: ZVector3F, up: ZVector3F, lens: ZLens) {
        this.lookAt.copy(lookAt)
        this.up.copy(up)
        this.lens = lens
    }

    @JsName("defaultCtor")
    constructor(lookAt: ZVector3F, up: ZVector3F): this(lookAt, up, ZPerspectiveLens.Default) {

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
