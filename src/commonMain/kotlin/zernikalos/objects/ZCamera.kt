package zernikalos.objects

import zernikalos.ZSceneContext
import zernikalos.ZRenderingContext
import zernikalos.components.camera.ZDefaultPerspectiveLens
import zernikalos.components.camera.ZLens
import zernikalos.components.camera.ZPerspectiveLens
import zernikalos.math.ZMatrix4F
import zernikalos.math.ZVector3F

class ZCamera: ZObject {

    val lookAt: ZVector3F = ZVector3F()
    val up: ZVector3F = ZVector3F()
    var lens: ZLens

    val projectionMatrix: ZMatrix4F
        get() = lens.projectionMatrix

    constructor(lookAt: ZVector3F, up: ZVector3F, lens: ZLens) {
        this.lookAt.copy(lookAt)
        this.up.copy(up)
        this.lens = lens
    }

    constructor(lookAt: ZVector3F, up: ZVector3F): this(lookAt, up, ZDefaultPerspectiveLens()) {

    }

    override fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
    }

    override fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
    }

}
