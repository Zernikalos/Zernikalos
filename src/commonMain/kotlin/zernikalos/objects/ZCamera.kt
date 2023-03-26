package zernikalos.objects

import zernikalos.ZSceneContext
import zernikalos.ZRenderingContext
import zernikalos.components.camera.ZLens
import zernikalos.math.ZVector3F

class ZCamera: ZObject {

    val lookAt: ZVector3F = ZVector3F()
    val up: ZVector3F = ZVector3F()
    lateinit var lens: ZLens

    constructor(lookAt: ZVector3F, up: ZVector3F) {
        this.lookAt.copy(lookAt)
        this.up.copy(up)
    }

    constructor(lookAt: ZVector3F, up: ZVector3F, lensType: ZLens.Types) {
        this.lookAt.copy(lookAt)
        this.up.copy(up)
    }

    override fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
    }

    override fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
    }

}
