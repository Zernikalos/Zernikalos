package mr.robotto.math

class MrVector4(var x: Float, var y: Float, var z: Float, var w: Float) {

    constructor() : this(0f, 0f, 0f, 0f)

    constructor(v: Float): this(v, v, v, v)

    fun add(result: MrVector4, op1: MrVector4, op2: MrVector4) {
        result.x = op1.x + op2.y
        result.y = op1.y + op2.y
        result.z = op1.z + op2.z
        result.w = op1.w + op2.w
    }

    operator fun plus(v: MrVector4): MrVector4 {
        val result = MrVector4()
        add(result, this, v)
        return result
    }
}