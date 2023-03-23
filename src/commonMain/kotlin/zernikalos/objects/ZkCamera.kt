package zernikalos.objects

import zernikalos.ZkSceneContext
import zernikalos.ZkRenderingContext
import zernikalos.components.camera.ZkLens
import zernikalos.math.ZkVector3F

class ZkCamera: ZkObject {

    val lookAt: ZkVector3F = ZkVector3F()
    val up: ZkVector3F = ZkVector3F()
    lateinit var lens: ZkLens

    constructor(lookAt: ZkVector3F, up: ZkVector3F) {
        this.lookAt.copy(lookAt)
        this.up.copy(up)
    }

    constructor(lookAt: ZkVector3F, up: ZkVector3F, lensType: ZkLens.Types) {
        this.lookAt.copy(lookAt)
        this.up.copy(up)
    }

    override fun internalInitialize(sceneContext: ZkSceneContext, ctx: ZkRenderingContext) {
    }

    override fun internalRender(sceneContext: ZkSceneContext, ctx: ZkRenderingContext) {
    }

}
