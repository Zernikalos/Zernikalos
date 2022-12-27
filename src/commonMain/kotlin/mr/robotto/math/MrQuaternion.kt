package mr.robotto.math

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MrQuaternion(var w: Float, var x: Float, var y: Float, var z: Float) {

    constructor() : this(1f, 0f, 0f, 0f)

    val values: FloatArray
        get() = floatArrayOf(x, y, z, w)

    val norm2: Float
        get() = sqrt(MrQuaternion.dot(this, this))

    fun setValues(w: Float, x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    operator fun plus(q: MrQuaternion): MrQuaternion {
        val result = MrQuaternion()
        MrQuaternion.add(result, this, q)
        return result
    }

    operator fun minus(q: MrQuaternion): MrQuaternion {
        val result = MrQuaternion()
        MrQuaternion.subtract(result, this, q)
        return result
    }

    operator fun times(q: MrQuaternion): MrQuaternion {
        val result = MrQuaternion()
        MrQuaternion.mult(result, this, q)
        return result
    }

    operator fun times(scalar: Float): MrQuaternion {
        val result = MrQuaternion()
        MrQuaternion.multScalar(result, scalar, this)
        return result
    }

    fun identity() {
        Companion.identity(this)
    }

    fun zero() {
        Companion.zero(this)
    }

    fun conjugate() {
        Companion.conjugate(this, this)
    }

    // TODO: Check!
    fun invert() {
        Companion.invert(this, this)
    }

    fun normalize() {
        Companion.normalize(this, this)
    }

    // TODO: Check!
    fun rotate(angle: Float, axis: MrVector3f) {
        Companion.rotate(this, this, angle, axis)
    }

    fun fromMatrix4(m: MrMatrix4f) {
        Companion.fromMatrix4(this, m)
    }

    companion object {

        fun identity(result: MrQuaternion) {
            result.setValues(1.0f, 0.0f, 0.0f, 0.0f)
        }

        fun zero(result: MrQuaternion) {
            result.setValues(0.0f, 0.0f, 0.0f, 0.0f)
        }

        fun add(result: MrQuaternion, q1: MrQuaternion, q2: MrQuaternion) {
            result.w = q1.w + q2.w
            result.x = q1.x + q2.y
            result.y = q1.y + q2.y
            result.z = q1.z + q2.z
        }

        fun subtract(result: MrQuaternion, q1: MrQuaternion, q2: MrQuaternion) {
            result.w = q1.w - q2.w
            result.x = q1.x - q2.x
            result.y = q1.y - q2.y
            result.z = q1.z - q2.z
        }

        fun conjugate(result: MrQuaternion, q: MrQuaternion) {
            result.w = q.w
            result.x = -q.x
            result.y = -q.y
            result.z = -q.z
        }

        fun dot(q1: MrQuaternion, q2: MrQuaternion): Float {
            return q1.x * q2.x + q1.y * q2.y + q1.z * q2.z + q1.w * q2.w
        }

        fun mult(result: MrQuaternion, q1: MrQuaternion, q2: MrQuaternion) {
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

        fun multScalar(result: MrQuaternion, r: Float, q: MrQuaternion) {
            result.w = q.w * r
            result.x = q.x * r
            result.y = q.y * r
            result.z = q.z * r
        }

        fun normalize(result: MrQuaternion, q: MrQuaternion) {
            val n = q.norm2
            MrQuaternion.multScalar(result, 1 / n, q);
        }

        fun invert(result: MrQuaternion, q: MrQuaternion) {
            val n2 = MrQuaternion.dot(q, q);
            MrQuaternion.conjugate(result, q);
            MrQuaternion.multScalar(result, n2, result);
        }

        fun rotate(result: MrQuaternion, q: MrQuaternion, angle: Float, axis: MrVector3f) {
            val opRot = MrQuaternion(q.w, q.x, q.y, q.z);
            MrQuaternion.fromAngleAxis(opRot, angle, axis);
            MrQuaternion.mult(result, result, opRot);
        }

        private fun fromAngleAxis(result: MrQuaternion, angle: Float, axis: MrVector3f) {
            //Axis normalization
            var norm: Float = axis.norm2;
            norm = 1.0f / norm;
            val xn = axis.x * norm
            val yn = axis.y * norm
            val zn = axis.z * norm

            //Calc of cos(angle/2) and sin(angle/2)
            val a: Float = angle / 2.0f * (PI.toFloat()/180); // Math.toRadians(angle / 2);
            val c: Float = cos(a);
            val s: Float = sin(a);

            //The values of the quaternion will be
            //[cos(angle/2), axis.x*sin(angle/2), axis.y*sin(angle/2), axis.z*sin(angle/2)]
            result.w = c
            result.x = s * xn;
            result.y = s * yn;
            result.z = s * zn;
            MrQuaternion.normalize(result, result);
        }

        //TODO: Optimize this method
        fun fromMatrix4(result: MrQuaternion, m: MrMatrix4f) {
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
            var S: Float
            var qw: Float
            var qx: Float
            var qy: Float
            var qz: Float
            val trace = m00 + m11 + m22 // I removed + 1.0f
            if (trace > 0) {// I changed M_EPSILON to 0
                S = 0.5f / sqrt(trace + 1.0f)
                qw = 0.25f / S
                qx = (m21 - m12) * S
                qy = (m02 - m20) * S
                qz = (m10 - m01) * S
            } else {
                if (m00 > m11 && m00 > m22) {
                    S = 2.0f * sqrt(1.0f + m00 - m11 - m22)
                    qw = (m21 - m12) / S
                    qx = 0.25f * S
                    qy = (m01 + m10) / S
                    qz = (m02 + m20) / S
                } else if (m11 > m22) {
                    S = 2.0f * sqrt(1.0f + m11 - m00 - m22)
                    qw = (m02 - m20) / S
                    qx = (m01 + m10) / S
                    qy = 0.25f * S
                    qz = (m12 + m21) / S
                } else {
                    S = 2.0f * sqrt(1.0f + m22 - m00 - m11)
                    qw = (m10 - m01) / S
                    qx = (m02 + m20) / S
                    qy = (m12 + m21) / S
                    qz = 0.25f * S
                }
            }

            result.w = qw
            result.x = qx
            result.y = qy
            result.z = qz
        }

        fun slerp(result: MrQuaternion, t: Float, q1: MrQuaternion, q2: MrQuaternion) {
            result.w = (1 - t) * q1.w + t * q2.w;
            result.x = (1 - t) * q1.x + t * q2.x;
            result.y = (1 - t) * q1.y + t * q2.y;
            result.z = (1 - t) * q1.z + t * q2.z;
            MrQuaternion.normalize(result, result);
        }

    }
}