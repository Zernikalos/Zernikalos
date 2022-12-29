package mr.robotto.objects

import mr.robotto.MrRenderingContext
import mr.robotto.components.camera.MrLens
import mr.robotto.math.MrVector3f

class MrCamera: MrObject {

    val lookAt: MrVector3f = MrVector3f()
    val up: MrVector3f = MrVector3f()
    lateinit var lens: MrLens

    constructor(lookAt: MrVector3f, up: MrVector3f) {
        this.lookAt.copy(lookAt)
        this.up.copy(up)
    }

    constructor(lookAt: MrVector3f, up: MrVector3f, lensType: MrLens.Types) {
        this.lookAt.copy(lookAt)
        this.up.copy(up)
    }

    override fun internalInitialize(ctx: MrRenderingContext) {
    }

    override fun internalRender() {
    }


}