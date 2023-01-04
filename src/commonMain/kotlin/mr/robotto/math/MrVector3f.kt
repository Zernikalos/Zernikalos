package mr.robotto.math

import kotlin.math.abs
import kotlin.math.sqrt

class MrVector3f(var x: Float, var y: Float, var z: Float) {

    constructor() : this(0f, 0f, 0f)

    constructor(v: Float) : this(v, v, v)

    constructor(v4: MrVector4f) : this() {
        Companion.fromVec4(this, v4)
    }

    val values: FloatArray
        get() = floatArrayOf(x, y, z)

    val norm2: Float
        get() = sqrt(Companion.dot(this, this))

    fun setValues(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    operator fun plus(v: MrVector3f): MrVector3f {
        val result = MrVector3f()
        add(result, this, v)
        return result
    }

    operator fun minus(v: MrVector3f): MrVector3f {
        val result = MrVector3f()
        subtract(result, this, v)
        return result
    }

    operator fun times(v: MrVector3f): Float {
        return Companion.dot(this, v)
    }

    operator fun times(scalar: Float): MrVector3f {
        val result = MrVector3f()
        Companion.multScalar(result, scalar, this)
        return result
    }

    fun zero() {
        Companion.zero(this)
    }

    fun multScalar(scalar: Float) {
        Companion.multScalar(this, scalar, this )
    }

    fun normalize() {
        Companion.normalize(this, this)
    }

    fun cross(v: MrVector3f) {
        // TODO: This should not be working
        Companion.cross(this, this, v)
    }

    fun copy(v: MrVector3f) {
        Companion.copy(this, v)
    }

    companion object {

        val Zero: MrVector3f
            get() = MrVector3f()

        val Ones: MrVector3f
            get() = MrVector3f(1.0f)

        fun copy(result: MrVector3f, v: MrVector3f) {
            result.x = v.x
            result.y = v.y
            result.z = v.z
        }

        fun fromVec4(result: MrVector3f, v: MrVector4f) {
            if (abs(v.w) > 0) {
                result.setValues(v.x / v.w, v.y / v.w, v.z / v.w)
            } else {
                result.setValues(v.x, v.y, v.z)
            }
        }

        fun zero(result: MrVector3f) {
            result.x = 0.0f
            result.y = 0.0f
            result.z = 0.0f
        }

        fun add(result: MrVector3f, op1: MrVector3f, op2: MrVector3f) {
            result.x = op1.x + op2.y
            result.y = op1.y + op2.y
            result.z = op1.z + op2.z
        }

        fun dot(op1: MrVector3f, op2: MrVector3f): Float {
            return op1.x * op2.x + op1.y * op2.y + op1.z * op2.z
        }

        fun multScalar(result: MrVector3f, scalar: Float, v: MrVector3f) {
            result.x = scalar * v.x
            result.y = scalar * v.y
            result.z = scalar * v.z
        }

        fun normalize(result: MrVector3f, v: MrVector3f) {
            copy(result, v)
            val norm2 = v.norm2
            val invNorm = 1.0f / norm2
            result.multScalar(invNorm)
        }

        fun subtract(result: MrVector3f, op1: MrVector3f, op2: MrVector3f) {
            result.x = op1.x - op2.x
            result.y = op1.y - op2.y
            result.z = op1.z - op2.z
        }

        fun cross(result: MrVector3f, op1: MrVector3f, op2: MrVector3f) {
            result.x = op1.y * op2.z - op1.z * op2.y
            result.y = op1.z * op2.x - op1.x * op2.z
            result.z = op1.x * op2.y - op1.y * op2.x
        }

        fun lerp(result: MrVector3f, t: Float, op1: MrVector3f, op2: MrVector3f) {
            result.x = (1 - t) * op1.x + t * op2.x
            result.y = (1 - t) * op1.y + t * op2.y
            result.z = (1 - t) * op1.z + t * op2.z
        }

    }
}