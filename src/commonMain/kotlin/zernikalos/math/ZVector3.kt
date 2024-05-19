package zernikalos.math

import kotlinx.serialization.Serializable
import zernikalos.ZDataType
import zernikalos.ZTypes
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.abs
import kotlin.math.sqrt

@JsExport
@Serializable
class ZVector3(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f): ZAlgebraObject {

    @JsName("init")
    constructor() : this(0f, 0f, 0f)

    @JsName("initWithValue")
    constructor(v: Float) : this(v, v, v)

    @JsName("initWithVec4")
    constructor(v4: ZVector4) : this() {
        fromVec4(this, v4)
    }

    override val values: FloatArray
        get() = floatArrayOf(x, y, z)

    val norm2: Float
        get() = sqrt(dot(this, this))

    override val size: Int
        get() = 3

    override val count: Int
        get() = 1

    override val dataType: ZDataType
        get() = ZTypes.VEC3F

    val normalized: ZVector3
        get() {
            val result = ZVector3()
            normalize(result, this)
            return result
        }

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

    operator fun plus(v: ZVector3): ZVector3 {
        val result = ZVector3()
        add(result, this, v)
        return result
    }

    operator fun minus(v: ZVector3): ZVector3 {
        val result = ZVector3()
        subtract(result, this, v)
        return result
    }

    operator fun times(v: ZVector3): Float {
        return dot(this, v)
    }

    @JsName("timesScalar")
    operator fun times(scalar: Float): ZVector3 {
        val result = ZVector3()
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

    fun cross(v: ZVector3) {
        // TODO: This should not be working
        cross(this, this, v)
    }

    fun copy(v: ZVector3) {
        copy(this, v)
    }

    override fun toString(): String {
        return "[$x, $y, $z]"
    }

    companion object Op {

        val Zero: ZVector3
            get() = ZVector3()

        val Ones: ZVector3
            get() = ZVector3(1.0f)

        val Forward: ZVector3
            get() = ZVector3(1f, 0f, 0f)

        val Right: ZVector3
            get() = ZVector3(0f, 1f, 0f)

        val Up: ZVector3
            get() = ZVector3(0f, 0f, 1f)

        fun copy(result: ZVector3, v: ZVector3) {
            result.x = v.x
            result.y = v.y
            result.z = v.z
        }

        fun fromVec4(result: ZVector3, v: ZVector4) {
            if (abs(v.w) > 0) {
                result.setValues(v.x / v.w, v.y / v.w, v.z / v.w)
            } else {
                result.setValues(v.x, v.y, v.z)
            }
        }

        fun zero(result: ZVector3) {
            result.x = 0.0f
            result.y = 0.0f
            result.z = 0.0f
        }

        fun add(result: ZVector3, op1: ZVector3, op2: ZVector3) {
            result.x = op1.x + op2.y
            result.y = op1.y + op2.y
            result.z = op1.z + op2.z
        }

        fun dot(op1: ZVector3, op2: ZVector3): Float {
            return op1.x * op2.x + op1.y * op2.y + op1.z * op2.z
        }

        fun norm2(v: ZVector3): Float {
            return sqrt(dot(v, v))
        }

        @JsName("norm2PerValue")
        fun norm2(x: Float, y: Float, z: Float): Float {
            return sqrt(x * x + y * y + z * z)
        }

        fun multScalar(result: ZVector3, scalar: Float, v: ZVector3) {
            result.x = scalar * v.x
            result.y = scalar * v.y
            result.z = scalar * v.z
        }

        fun normalize(result: ZVector3, v: ZVector3) {
            copy(result, v)
            val norm2 = v.norm2
            if (abs(norm2) < 0.00000000001) {
                return
            }
            val invNorm = 1.0f / norm2
            result.multScalar(invNorm)
        }

        fun subtract(result: ZVector3, op1: ZVector3, op2: ZVector3) {
            result.x = op1.x - op2.x
            result.y = op1.y - op2.y
            result.z = op1.z - op2.z
        }

        fun cross(result: ZVector3, op1: ZVector3, op2: ZVector3) {
            result.x = op1.y * op2.z - op1.z * op2.y
            result.y = op1.z * op2.x - op1.x * op2.z
            result.z = op1.x * op2.y - op1.y * op2.x
        }

        fun lerp(result: ZVector3, t: Float, op1: ZVector3, op2: ZVector3) {
            result.x = (1 - t) * op1.x + t * op2.x
            result.y = (1 - t) * op1.y + t * op2.y
            result.z = (1 - t) * op1.z + t * op2.z
        }

        fun rotateVector(result: ZVector3, q: ZQuaternion, v: ZVector3) {
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
