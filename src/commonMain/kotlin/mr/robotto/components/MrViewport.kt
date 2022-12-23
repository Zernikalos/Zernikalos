package mr.robotto.components

import mr.robotto.BufferBit
import mr.robotto.math.MrVector4

class MrViewport: MrComponent() {
    val viewport: Array<Int> = arrayOf(0, 0, 700, 700)
    val clearColor: MrVector4 = MrVector4(.2f, .2f, .2f, 1.0f)
    val clearMask: Int = BufferBit.COLOR_BUFFER.value or BufferBit.DEPTH_BUFFER.value

    override fun renderInitialize() {
        val vp = viewport
        context.viewport(0, 0, 700, 700)
    }

    override fun render() {
        val v = clearColor
        context.clearColor(v.x, v.y, v.z, v.w)
        context.clear(clearMask)
    }
}
