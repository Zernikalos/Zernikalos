package zernikalos.math

import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.sqrt

@JsExport
@Serializable
class ZVector4F(var x: Float, var y: Float, var z: Float, var w: Float): ZAlgebraObject {

    @JsName("zeroCtor")
    constructor() : this(0f, 0f, 0f, 0f)

    @JsName("repeatCtor")
    constructor(v: Float): this(v, v, v, v)

    @JsName("copyCtor")
    constructor(v: ZVector3F) : this(v.x, v.y, v.z, 1.0f)

    override val values: FloatArray
        get() = floatArrayOf(x, y, z, w)

    val norm2: Float
        get() = sqrt(dot(this, this))

    override val size: Int
        get() = 4

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

    operator fun plus(v: ZVector4F): ZVector4F {
        val result = ZVector4F()
        add(result, this, v)
        return result
    }

    operator fun minus(v: ZVector4F): ZVector4F {
        val result = ZVector4F()
        subtract(result, this, v)
        return result
    }

    operator fun times(v: ZVector4F): Float {
        return dot(this, v)
    }

    @JsName("timesScalar")
    operator fun times(scalar: Float): ZVector4F {
        val result = ZVector4F()
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

    companion object {
        fun add(result: ZVector4F, op1: ZVector4F, op2: ZVector4F) {
            result.x = op1.x + op2.y
            result.y = op1.y + op2.y
            result.z = op1.z + op2.z
            result.w = op1.w + op2.w
        }

        fun copy(result: ZVector4F, v: ZVector4F) {
            result.x = v.x
            result.y = v.y
            result.z = v.z
            result.w = v.w
        }

        fun fromVec3(result: ZVector4F, v: ZVector3F) {
            result.setValues(v.x, v.y, v.z, 1.0f)
        }

        fun zero(result: ZVector4F) {
            result.x = 0.0f
            result.y = 0.0f
            result.z = 0.0f
            result.w = 0.0f
        }

        fun dot(op1: ZVector4F, op2: ZVector4F): Float {
            return op1.x * op2.x + op1.y * op2.y + op1.z * op2.z + op1.w * op2.w
        }

        fun multScalar(result: ZVector4F, scalar: Float, v: ZVector4F) {
            result.x = scalar * v.x
            result.y = scalar * v.y
            result.z = scalar * v.z
            result.w = scalar * v.w
        }

        fun normalize(result: ZVector4F, v: ZVector4F) {
            copy(result, v)
            val norm2 = v.norm2
            val invNorm = 1.0f / norm2
            result.multScalar(invNorm)
        }

        fun subtract(result: ZVector4F, op1: ZVector4F, op2: ZVector4F) {
            result.x = op1.x - op2.x
            result.y = op1.y - op2.y
            result.z = op1.z - op2.z
            result.w = op1.w - op2.w
        }

        fun lerp(result: ZVector4F, t: Float, op1: ZVector4F, op2: ZVector4F) {
            result.x = (1 - t) * op1.x + t * op2.x
            result.y = (1 - t) * op1.y + t * op2.y
            result.z = (1 - t) * op1.z + t * op2.z
            result.w = (1 - t) * op1.w + t * op2.w
        }
    }
}