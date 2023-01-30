package mr.robotto.components

import mr.robotto.BufferBit
import mr.robotto.MrRenderingContext
import mr.robotto.math.MrVector4f

class MrViewport: MrComponent() {
    private val viewport: Array<Int> = arrayOf(0, 0, 700, 700)
    private val clearColor: MrVector4f = MrVector4f(.2f, .2f, .2f, 1.0f)
    private val clearMask: Int = BufferBit.COLOR_BUFFER.value or BufferBit.DEPTH_BUFFER.value

    override fun initialize(ctx: MrRenderingContext) {
        val vp = viewport
        ctx.viewport(0, 0, 700, 700)
    }

    override fun render(ctx: MrRenderingContext) {
        val v = clearColor
        ctx.clearColor(v.x, v.y, v.z, v.w)
        ctx.clear(clearMask)
    }
}
