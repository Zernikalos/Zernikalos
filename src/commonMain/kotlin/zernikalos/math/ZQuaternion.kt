package zernikalos.math

import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@JsExport
@Serializable
class ZQuaternion(var w: Float, var x: Float, var y: Float, var z: Float): ZAlgebraObject {

    override val values: FloatArray
        get() = floatArrayOf(w, x, y, z)

    override val size: Int
        get() = 4

    val norm2: Float
        get() = sqrt(dot(this, this))

    @JsName("identityCtor")
    constructor() : this(1f, 0f, 0f, 0f)

    fun setValues(w: Float, x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    operator fun plus(q: ZQuaternion): ZQuaternion {
        val result = ZQuaternion()
        add(result, this, q)
        return result
    }

    operator fun minus(q: ZQuaternion): ZQuaternion {
        val result = ZQuaternion()
        subtract(result, this, q)
        return result
    }

    operator fun times(q: ZQuaternion): ZQuaternion {
        val result = ZQuaternion()
        mult(result, this, q)
        return result
    }

    @JsName("timesScalar")
    operator fun times(scalar: Float): ZQuaternion {
        val result = ZQuaternion()
        multScalar(result, scalar, this)
        return result
    }

    fun identity() {
        identity(this)
    }

    fun zero() {
        zero(this)
    }

    fun conjugate() {
        conjugate(this, this)
    }

    // TODO: Check!
    fun invert() {
        invert(this, this)
    }

    fun normalize() {
        normalize(this, this)
    }

    // TODO: Check!
    fun rotate(angle: Float, axis: ZVector3) {
        rotate(this, this, angle, axis)
    }

    fun fromMatrix4(m: ZMatrix4) {
        fromMatrix4(this, m)
    }

    fun copy(q: ZQuaternion) {
        copy(this, q)
    }

    override fun toString(): String {
        return "[$w : $x, $y, $z]"
    }


    companion object {

        fun copy(result: ZQuaternion, q: ZQuaternion) {
            result.w = q.w
            result.x = q.x
            result.y = q.y
            result.z = q.z
        }

        val Identity: ZQuaternion
            get() = ZQuaternion()

        fun identity(result: ZQuaternion) {
            result.setValues(1.0f, 0.0f, 0.0f, 0.0f)
        }

        fun zero(result: ZQuaternion) {
            result.setValues(0.0f, 0.0f, 0.0f, 0.0f)
        }

        fun add(result: ZQuaternion, q1: ZQuaternion, q2: ZQuaternion) {
            result.w = q1.w + q2.w
            result.x = q1.x + q2.y
            result.y = q1.y + q2.y
            result.z = q1.z + q2.z
        }

        fun subtract(result: ZQuaternion, q1: ZQuaternion, q2: ZQuaternion) {
            result.w = q1.w - q2.w
            result.x = q1.x - q2.x
            result.y = q1.y - q2.y
            result.z = q1.z - q2.z
        }

        fun conjugate(result: ZQuaternion, q: ZQuaternion) {
            result.w = q.w
            result.x = -q.x
            result.y = -q.y
            result.z = -q.z
        }

        fun dot(q1: ZQuaternion, q2: ZQuaternion): Float {
            return q1.x * q2.x + q1.y * q2.y + q1.z * q2.z + q1.w * q2.w
        }

        fun mult(result: ZQuaternion, q1: ZQuaternion, q2: ZQuaternion) {
            val x1 = q1.x
            val y1 = q1.y
            val z1 = q1.z
            val w1 = q1.w

            val x2 = q2.x
            val y2 = q2.y
            val z2 = q2.z
            val w2 = q2.w
            result.w = w1 * w2 - x1 * x2 - y1 * y2 - z1 * z2
            result.x = w1 * x2 + x1 * w2 + y1 * z2 - z1 * y2
            result.y = w1 * y2 + y1 * w2 + z1 * x2 - x1 * z2
            result.z = w1 * z2 + z1 * w2 + x1 * y2 - y1 * x2
        }

        fun multScalar(result: ZQuaternion, r: Float, q: ZQuaternion) {
            result.w = q.w * r
            result.x = q.x * r
            result.y = q.y * r
            result.z = q.z * r
        }

        fun normalize(result: ZQuaternion, q: ZQuaternion) {
            val n = q.norm2
            multScalar(result, 1 / n, q)
        }

        fun invert(result: ZQuaternion, q: ZQuaternion) {
            val n2 = dot(q, q)
            conjugate(result, q)
            multScalar(result, n2, result)
        }

        fun rotate(result: ZQuaternion, q: ZQuaternion, angle: Float, axis: ZVector3) {
            val opRot = ZQuaternion(q.w, q.x, q.y, q.z)
            fromAngleAxis(opRot, angle, axis)
            mult(result, result, opRot)
        }

        fun fromVec3(result: ZQuaternion, v: ZVector3) {
            result.w = 0f
            result.x = v.x
            result.y = v.y
            result.z = v.z
        }

        fun fromAngleAxis(result: ZQuaternion, angle: Float, axis: ZVector3) {
            fromAngleAxis(result, angle, axis.x, axis.y, axis.z)
        }

        @JsName("fromAngleAxisPerValue")
        fun fromAngleAxis(result: ZQuaternion, angle: Float, x: Float, y: Float, z: Float) {
            //Axis normalization
            var norm: Float = ZVector3.norm2(x, y, z)
            norm = 1.0f / norm
            val xn = x * norm
            val yn = y * norm
            val zn = z * norm

            //Calc of cos(angle/2) and sin(angle/2)
            val a: Float = angle / 2.0f * (PI.toFloat()/180) // Math.toRadians(angle / 2)
            val c: Float = cos(a)
            val s: Float = sin(a)

            //The values of the quaternion will be
            //[cos(angle/2), axis.x*sin(angle/2), axis.y*sin(angle/2), axis.z*sin(angle/2)]
            result.w = c
            result.x = s * xn
            result.y = s * yn
            result.z = s * zn
            normalize(result, result)
        }

        //TODO: Optimize this method
        fun fromMatrix4(result: ZQuaternion, m: ZMatrix4) {
            val v = m.values
            val m00 = v[0]
            val m01 = v[4]
            val m02 = v[8]
            val m10 = v[1]
            val m11 = v[5]
            val m12 = v[9]
            val m20 = v[2]
            val m21 = v[6]
            val m22 = v[10]
            val s: Float
            val qw: Float
            val qx: Float
            val qy: Float
            val qz: Float
            val trace = m00 + m11 + m22 // I removed + 1.0f
            if (trace > 0) {// I changed M_EPSILON to 0
                s = 0.5f / sqrt(trace + 1.0f)
                qw = 0.25f / s
                qx = (m21 - m12) * s
                qy = (m02 - m20) * s
                qz = (m10 - m01) * s
            } else {
                if (m00 > m11 && m00 > m22) {
                    s = 2.0f * sqrt(1.0f + m00 - m11 - m22)
                    qw = (m21 - m12) / s
                    qx = 0.25f * s
                    qy = (m01 + m10) / s
                    qz = (m02 + m20) / s
                } else if (m11 > m22) {
                    s = 2.0f * sqrt(1.0f + m11 - m00 - m22)
                    qw = (m02 - m20) / s
                    qx = (m01 + m10) / s
                    qy = 0.25f * s
                    qz = (m12 + m21) / s
                } else {
                    s = 2.0f * sqrt(1.0f + m22 - m00 - m11)
                    qw = (m10 - m01) / s
                    qx = (m02 + m20) / s
                    qy = (m12 + m21) / s
                    qz = 0.25f * s
                }
            }

            result.w = qw
            result.x = qx
            result.y = qy
            result.z = qz
        }

        fun slerp(result: ZQuaternion, t: Float, q1: ZQuaternion, q2: ZQuaternion) {
            result.w = (1 - t) * q1.w + t * q2.w
            result.x = (1 - t) * q1.x + t * q2.x
            result.y = (1 - t) * q1.y + t * q2.y
            result.z = (1 - t) * q1.z + t * q2.z
            normalize(result, result)
        }

    }
}
