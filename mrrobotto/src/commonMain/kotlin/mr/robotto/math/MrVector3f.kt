package mr.robotto.math

import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.abs
import kotlin.math.sqrt

@JsExport
@Serializable
class MrVector3f(var x: Float, var y: Float, var z: Float): MrAlgebraObject {

    @JsName("zeroCtor")
    constructor() : this(0f, 0f, 0f)

    @JsName("repeatCtor")
    constructor(v: Float) : this(v, v, v)

    @JsName("copyCtor")
    constructor(v4: MrVector4f) : this() {
        Companion.fromVec4(this, v4)
    }

    override val values: FloatArray
        get() = floatArrayOf(x, y, z)

    val norm2: Float
        get() = sqrt(Companion.dot(this, this))

    override val size: Int
        get() = 3

    operator fun set(i: Int, value: Float) {
        when(i) {
            0 -> x = value
            1 -> y = value
            2 -> z = value
            else -> throw Error("Out of bounds array access")
        }
    }

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

    @JsName("timesScalar")
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

        fun norm2(v: MrVector3f): Float {
            return sqrt(dot(v, v))
        }

        @JsName("norm2PerValue")
        fun norm2(x: Float, y: Float, z: Float): Float {
            return x * x + y * y + z * z
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

        fun rotateVector(result: MrVector3f, q: MrQuaternion, v: MrVector3f) {
            val q1 = MrQuaternion()
            val q2 = MrQuaternion()

            //Qv = Q(0,vx,vy,vz)
            MrQuaternion.fromVec3(q1, v)
            //q=q/||q||
            MrQuaternion.normalize(q2, q)

            //q*Qv
            MrQuaternion.mult(q1, q2, q1)
            //q^
            MrQuaternion.conjugate(q2, q2)
            //q*Qv*q^
            MrQuaternion.mult(q1, q1, q2)
            result.setValues(q1.x, q1.y, q1.z)
        }

    }
}