package mr.robotto.components

import mr.robotto.BufferBit
import mr.robotto.math.MrVector4

class MrScene: MrComponent<MrSceneData, MrSceneRender>() {

    override var data: MrSceneData = MrSceneData()
    override var renderer: MrSceneRender = MrSceneRender()

}

class MrSceneData: MrComponentData() {
    val viewport: Array<Int> = arrayOf(0, 0, 700, 700)
    val clearColor: MrVector4 = MrVector4(.5f, .5f, .5f, 1.0f)
    val clearMask: Int = BufferBit.COLOR_BUFFER or BufferBit.DEPTH_BUFFER
}

class MrSceneRender: MrComponentRender<MrSceneData>() {
    override fun internalInitialize() {
        val vp = data.viewport
        context.viewport(0, 0, 700, 700)
    }

    override fun render() {
        val v = data.clearColor
        context.clearColor(v.x, v.y, v.z, v.w)
        context.clear(data.clearMask)
    }

}
