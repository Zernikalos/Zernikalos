package zernikalos.math

import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.abs
import kotlin.math.sqrt

@JsExport
@Serializable
class ZVector3F(var x: Float, var y: Float, var z: Float): ZAlgebraObject {

    @JsName("zeroCtor")
    constructor() : this(0f, 0f, 0f)

    @JsName("repeatCtor")
    constructor(v: Float) : this(v, v, v)

    @JsName("copyCtor")
    constructor(v4: ZVector4F) : this() {
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

    operator fun plus(v: ZVector3F): ZVector3F {
        val result = ZVector3F()
        add(result, this, v)
        return result
    }

    operator fun minus(v: ZVector3F): ZVector3F {
        val result = ZVector3F()
        subtract(result, this, v)
        return result
    }

    operator fun times(v: ZVector3F): Float {
        return dot(this, v)
    }

    @JsName("timesScalar")
    operator fun times(scalar: Float): ZVector3F {
        val result = ZVector3F()
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

    fun cross(v: ZVector3F) {
        // TODO: This should not be working
        cross(this, this, v)
    }

    fun copy(v: ZVector3F) {
        copy(this, v)
    }

    override fun toString(): String {
        return "[$x, $y, $z]"
    }

    companion object {

        val Zero: ZVector3F
            get() = ZVector3F()

        val Ones: ZVector3F
            get() = ZVector3F(1.0f)

        val Forward: ZVector3F
            get() = ZVector3F(1f, 0f, 0f)

        val Right: ZVector3F
            get() = ZVector3F(0f, 1f, 0f)

        val Up: ZVector3F
            get() = ZVector3F(0f, 0f, 1f)

        fun copy(result: ZVector3F, v: ZVector3F) {
            result.x = v.x
            result.y = v.y
            result.z = v.z
        }

        fun fromVec4(result: ZVector3F, v: ZVector4F) {
            if (abs(v.w) > 0) {
                result.setValues(v.x / v.w, v.y / v.w, v.z / v.w)
            } else {
                result.setValues(v.x, v.y, v.z)
            }
        }

        fun zero(result: ZVector3F) {
            result.x = 0.0f
            result.y = 0.0f
            result.z = 0.0f
        }

        fun add(result: ZVector3F, op1: ZVector3F, op2: ZVector3F) {
            result.x = op1.x + op2.y
            result.y = op1.y + op2.y
            result.z = op1.z + op2.z
        }

        fun dot(op1: ZVector3F, op2: ZVector3F): Float {
            return op1.x * op2.x + op1.y * op2.y + op1.z * op2.z
        }

        fun norm2(v: ZVector3F): Float {
            return sqrt(dot(v, v))
        }

        @JsName("norm2PerValue")
        fun norm2(x: Float, y: Float, z: Float): Float {
            return sqrt(x * x + y * y + z * z)
        }

        fun multScalar(result: ZVector3F, scalar: Float, v: ZVector3F) {
            result.x = scalar * v.x
            result.y = scalar * v.y
            result.z = scalar * v.z
        }

        fun normalize(result: ZVector3F, v: ZVector3F) {
            copy(result, v)
            val norm2 = v.norm2
            if (abs(norm2) < 0.00000000001) {
                return
            }
            val invNorm = 1.0f / norm2
            result.multScalar(invNorm)
        }

        fun subtract(result: ZVector3F, op1: ZVector3F, op2: ZVector3F) {
            result.x = op1.x - op2.x
            result.y = op1.y - op2.y
            result.z = op1.z - op2.z
        }

        fun cross(result: ZVector3F, op1: ZVector3F, op2: ZVector3F) {
            result.x = op1.y * op2.z - op1.z * op2.y
            result.y = op1.z * op2.x - op1.x * op2.z
            result.z = op1.x * op2.y - op1.y * op2.x
        }

        fun lerp(result: ZVector3F, t: Float, op1: ZVector3F, op2: ZVector3F) {
            result.x = (1 - t) * op1.x + t * op2.x
            result.y = (1 - t) * op1.y + t * op2.y
            result.z = (1 - t) * op1.z + t * op2.z
        }

        fun rotateVector(result: ZVector3F, q: ZQuaternion, v: ZVector3F) {
            val q1 = ZQuaternion()
            val q2 = ZQuaternion()

            //Qv = Q(0,vx,vy,vz)
            ZQuaternion.fromVec3(q1, v)
            //q=q/||q||
            ZQuaternion.normalize(q2, q)

            //q*Qv
            ZQuaternion.mult(q1, q2, q1)
            //q^
            ZQuaternion.conjugate(q2, q2)
            //q*Qv*q^
            ZQuaternion.mult(q1, q1, q2)
            result.setValues(q1.x, q1.y, q1.z)
        }

    }
}
