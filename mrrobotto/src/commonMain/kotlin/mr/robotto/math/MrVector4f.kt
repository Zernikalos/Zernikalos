package mr.robotto.math

import kotlin.math.abs
import kotlin.math.sqrt

class MrVector4f(var x: Float, var y: Float, var z: Float, var w: Float) {

    constructor() : this(0f, 0f, 0f, 0f)

    constructor(v: Float): this(v, v, v, v)

    constructor(v: MrVector3f) : this(v.x, v.y, v.z, 1.0f)

    val values: FloatArray
        get() = floatArrayOf(x, y, z, w)

    val norm2: Float
        get() = sqrt(MrVector4f.dot(this, this))

    fun setValues(x: Float, y: Float, z: Float, w: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    operator fun set(i: Int, value: Float) {
        when(i) {
            0 -> x = value
            1 -> y = value
            2 -> z = value
            3 -> w = value
            else -> throw Error("Out of bounds array access")
        }
    }

    operator fun plus(v: MrVector4f): MrVector4f {
        val result = MrVector4f()
        add(result, this, v)
        return result
    }

    operator fun minus(v: MrVector4f): MrVector4f {
        val result = MrVector4f()
        MrVector4f.subtract(result, this, v)
        return result
    }

    operator fun times(v: MrVector4f): Float {
        return MrVector4f.dot(this, v)
    }

    operator fun times(scalar: Float): MrVector4f {
        val result = MrVector4f()
        MrVector4f.multScalar(result, scalar, this)
        return result
    }

    fun zero() {
        MrVector4f.zero(this)
    }

    fun multScalar(scalar: Float) {
        MrVector4f.multScalar(this, scalar, this )
    }

    fun normalize() {
        MrVector4f.normalize(this, this)
    }

    companion object {
        fun add(result: MrVector4f, op1: MrVector4f, op2: MrVector4f) {
            result.x = op1.x + op2.y
            result.y = op1.y + op2.y
            result.z = op1.z + op2.z
            result.w = op1.w + op2.w
        }

        fun copy(result: MrVector4f, v: MrVector4f) {
            result.x = v.x
            result.y = v.y
            result.z = v.z
            result.w = v.w
        }

        fun fromVec3(result: MrVector4f, v: MrVector3f) {
            result.setValues(v.x, v.y, v.z, 1.0f)
        }

        fun zero(result: MrVector4f) {
            result.x = 0.0f
            result.y = 0.0f
            result.z = 0.0f
            result.w = 0.0f
        }

        fun dot(op1: MrVector4f, op2: MrVector4f): Float {
            return op1.x * op2.x + op1.y * op2.y + op1.z * op2.z + op1.w * op2.w
        }

        fun multScalar(result: MrVector4f, scalar: Float, v: MrVector4f) {
            result.x = scalar * v.x
            result.y = scalar * v.y
            result.z = scalar * v.z
            result.w = scalar * v.w
        }

        fun normalize(result: MrVector4f, v: MrVector4f) {
            copy(result, v)
            val norm2 = v.norm2
            val invNorm = 1.0f / norm2
            result.multScalar(invNorm)
        }

        fun subtract(result: MrVector4f, op1: MrVector4f, op2: MrVector4f) {
            result.x = op1.x - op2.x
            result.y = op1.y - op2.y
            result.z = op1.z - op2.z
            result.w = op1.w - op2.w
        }

        fun lerp(result: MrVector4f, t: Float, op1: MrVector4f, op2: MrVector4f) {
            result.x = (1 - t) * op1.x + t * op2.x
            result.y = (1 - t) * op1.y + t * op2.y
            result.z = (1 - t) * op1.z + t * op2.z
            result.w = (1 - t) * op1.w + t * op2.w
        }
    }
}