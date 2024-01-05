package zernikalos.math

import kotlinx.serialization.Serializable
import zernikalos.ZDataType
import zernikalos.ZTypes
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.sqrt

@JsExport
@Serializable
class ZVector4(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f, var w: Float = 0f): ZAlgebraObject {

    @JsName("init")
    constructor() : this(0f, 0f, 0f, 0f)

    @JsName("initWithValue")
    constructor(v: Float): this(v, v, v, v)

    @JsName("initWithVec3")
    constructor(v: ZVector3) : this(v.x, v.y, v.z, 1.0f)

    override val values: FloatArray
        get() = floatArrayOf(x, y, z, w)

    val norm2: Float
        get() = sqrt(dot(this, this))

    override val size: Int
        get() = 4

    override val count: Int
        get() = 1

    override val dataType: ZDataType
        get() = ZTypes.VEC4F

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

    operator fun plus(v: ZVector4): ZVector4 {
        val result = ZVector4()
        add(result, this, v)
        return result
    }

    operator fun minus(v: ZVector4): ZVector4 {
        val result = ZVector4()
        subtract(result, this, v)
        return result
    }

    operator fun times(v: ZVector4): Float {
        return dot(this, v)
    }

    @JsName("timesScalar")
    operator fun times(scalar: Float): ZVector4 {
        val result = ZVector4()
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

    companion object Op {
        fun add(result: ZVector4, op1: ZVector4, op2: ZVector4) {
            result.x = op1.x + op2.y
            result.y = op1.y + op2.y
            result.z = op1.z + op2.z
            result.w = op1.w + op2.w
        }

        fun copy(result: ZVector4, v: ZVector4) {
            result.x = v.x
            result.y = v.y
            result.z = v.z
            result.w = v.w
        }

        fun fromVec3(result: ZVector4, v: ZVector3) {
            result.setValues(v.x, v.y, v.z, 1.0f)
        }

        fun zero(result: ZVector4) {
            result.x = 0.0f
            result.y = 0.0f
            result.z = 0.0f
            result.w = 0.0f
        }

        fun dot(op1: ZVector4, op2: ZVector4): Float {
            return op1.x * op2.x + op1.y * op2.y + op1.z * op2.z + op1.w * op2.w
        }

        fun multScalar(result: ZVector4, scalar: Float, v: ZVector4) {
            result.x = scalar * v.x
            result.y = scalar * v.y
            result.z = scalar * v.z
            result.w = scalar * v.w
        }

        fun normalize(result: ZVector4, v: ZVector4) {
            copy(result, v)
            val norm2 = v.norm2
            val invNorm = 1.0f / norm2
            result.multScalar(invNorm)
        }

        fun subtract(result: ZVector4, op1: ZVector4, op2: ZVector4) {
            result.x = op1.x - op2.x
            result.y = op1.y - op2.y
            result.z = op1.z - op2.z
            result.w = op1.w - op2.w
        }

        fun lerp(result: ZVector4, t: Float, op1: ZVector4, op2: ZVector4) {
            result.x = (1 - t) * op1.x + t * op2.x
            result.y = (1 - t) * op1.y + t * op2.y
            result.z = (1 - t) * op1.z + t * op2.z
            result.w = (1 - t) * op1.w + t * op2.w
        }
    }
}