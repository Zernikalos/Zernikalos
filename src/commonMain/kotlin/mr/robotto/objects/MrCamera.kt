package mr.robotto.objects

import mr.robotto.MrRenderingContext
import mr.robotto.components.camera.MrLens
import mr.robotto.math.MrVector3f

class MrCamera: MrObject() {

    lateinit var lookAt: MrVector3f
    lateinit var up: MrVector3f
    lateinit var lens: MrLens

    override fun internalInitialize(ctx: MrRenderingContext) {
    }

    override fun internalRender() {
    }


}