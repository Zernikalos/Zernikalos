package zernikalos.math

import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.abs
import kotlin.math.sqrt

@JsExport
@Serializable
class ZkVector3F(var x: Float, var y: Float, var z: Float): ZkAlgebraObject {

    @JsName("zeroCtor")
    constructor() : this(0f, 0f, 0f)

    @JsName("repeatCtor")
    constructor(v: Float) : this(v, v, v)

    @JsName("copyCtor")
    constructor(v4: ZkVector4F) : this() {
        fromVec4(this, v4)
    }

    override val values: FloatArray
        get() = floatArrayOf(x, y, z)

    val norm2: Float
        get() = sqrt(dot(this, this))

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

    operator fun plus(v: ZkVector3F): ZkVector3F {
        val result = ZkVector3F()
        add(result, this, v)
        return result
    }

    operator fun minus(v: ZkVector3F): ZkVector3F {
        val result = ZkVector3F()
        subtract(result, this, v)
        return result
    }

    operator fun times(v: ZkVector3F): Float {
        return dot(this, v)
    }

    @JsName("timesScalar")
    operator fun times(scalar: Float): ZkVector3F {
        val result = ZkVector3F()
        multScalar(result, scalar, this)
        return result
    }

    fun zero() {
        zero(this)
    }

    fun multScalar(scalar: Float) {
        multScalar(this, scalar, this )
    }

    fun normalize() {
        normalize(this, this)
    }

    fun cross(v: ZkVector3F) {
        // TODO: This should not be working
        cross(this, this, v)
    }

    fun copy(v: ZkVector3F) {
        copy(this, v)
    }

    companion object {

        val Zero: ZkVector3F
            get() = ZkVector3F()

        val Ones: ZkVector3F
            get() = ZkVector3F(1.0f)

        fun copy(result: ZkVector3F, v: ZkVector3F) {
            result.x = v.x
            result.y = v.y
            result.z = v.z
        }

        fun fromVec4(result: ZkVector3F, v: ZkVector4F) {
            if (abs(v.w) > 0) {
                result.setValues(v.x / v.w, v.y / v.w, v.z / v.w)
            } else {
                result.setValues(v.x, v.y, v.z)
            }
        }

        fun zero(result: ZkVector3F) {
            result.x = 0.0f
            result.y = 0.0f
            result.z = 0.0f
        }

        fun add(result: ZkVector3F, op1: ZkVector3F, op2: ZkVector3F) {
            result.x = op1.x + op2.y
            result.y = op1.y + op2.y
            result.z = op1.z + op2.z
        }

        fun dot(op1: ZkVector3F, op2: ZkVector3F): Float {
            return op1.x * op2.x + op1.y * op2.y + op1.z * op2.z
        }

        fun norm2(v: ZkVector3F): Float {
            return sqrt(dot(v, v))
        }

        @JsName("norm2PerValue")
        fun norm2(x: Float, y: Float, z: Float): Float {
            return sqrt(x * x + y * y + z * z)
        }

        fun multScalar(result: ZkVector3F, scalar: Float, v: ZkVector3F) {
            result.x = scalar * v.x
            result.y = scalar * v.y
            result.z = scalar * v.z
        }

        fun normalize(result: ZkVector3F, v: ZkVector3F) {
            copy(result, v)
            val norm2 = v.norm2
            val invNorm = 1.0f / norm2
            result.multScalar(invNorm)
        }

        fun subtract(result: ZkVector3F, op1: ZkVector3F, op2: ZkVector3F) {
            result.x = op1.x - op2.x
            result.y = op1.y - op2.y
            result.z = op1.z - op2.z
        }

        fun cross(result: ZkVector3F, op1: ZkVector3F, op2: ZkVector3F) {
            result.x = op1.y * op2.z - op1.z * op2.y
            result.y = op1.z * op2.x - op1.x * op2.z
            result.z = op1.x * op2.y - op1.y * op2.x
        }

        fun lerp(result: ZkVector3F, t: Float, op1: ZkVector3F, op2: ZkVector3F) {
            result.x = (1 - t) * op1.x + t * op2.x
            result.y = (1 - t) * op1.y + t * op2.y
            result.z = (1 - t) * op1.z + t * op2.z
        }

        fun rotateVector(result: ZkVector3F, q: ZkQuaternion, v: ZkVector3F) {
            val q1 = ZkQuaternion()
            val q2 = ZkQuaternion()

            //Qv = Q(0,vx,vy,vz)
            ZkQuaternion.fromVec3(q1, v)
            //q=q/||q||
            ZkQuaternion.normalize(q2, q)

            //q*Qv
            ZkQuaternion.mult(q1, q2, q1)
            //q^
            ZkQuaternion.conjugate(q2, q2)
            //q*Qv*q^
            ZkQuaternion.mult(q1, q1, q2)
            result.setValues(q1.x, q1.y, q1.z)
        }

    }
}
