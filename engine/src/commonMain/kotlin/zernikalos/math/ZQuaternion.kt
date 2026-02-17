/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.utils.toByteArray
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.*

@JsExport
@Serializable(with = ZQuaternionSerializer::class)
class ZQuaternion(): ZAlgebraObject {

    private val _values = floatArrayOf(1f, 0f, 0f, 0f)

    var w: Float
        get() = _values[0]
        set(value) {
            _values[0] = value
        }

    var x: Float
        get() = _values[1]
        set(value) {
            _values[1] = value
        }

    var y: Float
        get() = _values[2]
        set(value) {
            _values[2] = value
        }

    var z: Float
        get() = _values[3]
        set(value) {
            _values[3] = value
        }


    @JsName("initWithValues")
    constructor(w: Float = 1f, x: Float = 0f, y: Float = 0f, z: Float = 0f) : this() {
        this.w = w
        this.x = x
        this.y = y
        this.z = z
    }

    override val floatArray: FloatArray
        get() = _values

    override val byteArray: ByteArray
        get() = floatArray.toByteArray()

    override val size: Int = 4

    override val count: Int = 1

    override val byteSize: Int
        get() = dataType.byteSize

    override val dataType: ZDataType = ZTypes.QUATERNION

    val norm2: Float
        get() = sqrt(dot(this, this))

    val isNormalized: Boolean
        get() = abs(norm2 - 1.0f) < 0.0001f

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

    /**
     * Normalizes the quaternion.
     *
     * This method modifies the quaternion.
     */
    fun normalize() {
        normalize(this, this)
    }

    /**
     * Rotates the quaternion by a specified angle around a specified axis.
     */
    fun rotate(angle: Float, axis: ZVector3) {
        rotate(this, this, angle, axis)
    }

    fun fromMatrix4(m: ZMatrix4) {
        fromMatrix4(this, m)
    }

    fun fromAngleAxis(angle: Float, x: Float, y: Float, z: Float) {
        fromAngleAxis(this, angle, x, y, z)
    }

    fun toMatrix4(): ZMatrix4 {
        val m = ZMatrix4()
        ZMatrix4.fromQuaternion(m, this)
        return m
    }

    fun toEuler(): ZEuler {
        val result = ZEuler()
        ZEuler.fromQuaternion(result, this)
        return result
    }

    fun copy(q: ZQuaternion) {
        copy(this, q)
    }

    override fun toString(): String {
        return "[$w : $x, $y, $z]"
    }

    companion object Op {

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
            result.x = q1.x + q2.x
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

        /**
         * Normalizes the quaternion.
         */
        fun normalize(result: ZQuaternion, q: ZQuaternion) {
            val n = q.norm2
            multScalar(result, 1 / n, q)
        }

        fun invert(result: ZQuaternion, q: ZQuaternion) {
            val n2 = dot(q, q)
            conjugate(result, q)
            multScalar(result, n2, result)
        }

        /**
         * Rotates a quaternion by a specified angle around a specified axis.
         */
        fun rotate(result: ZQuaternion, q: ZQuaternion, angle: Float, axis: ZVector3) {
            val opRot = ZQuaternion()
            fromAngleAxis(opRot, angle, axis)
            mult(result, q, opRot)
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

        @JsName("fromMatrix4Ip")
        fun fromMatrix4(m: ZMatrix4): ZQuaternion {
            val result = ZQuaternion()
            ZQuaternion.fromMatrix4(result, m)
            return result
        }

        //TODO: Optimize this method
        fun fromMatrix4(result: ZQuaternion, m: ZMatrix4) {
            val v = m.floatArray
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

        /**
         * Converts Euler angles to quaternion using ZYX (yaw-pitch-roll) convention.
         * @param result The quaternion to store the result in
         * @param euler The Euler angles to convert from
         */
        fun fromEuler(result: ZQuaternion, euler: ZEuler) {
            val degToRad = (kotlin.math.PI / 180.0).toFloat()
            // Convert angles to radians and divide by 2 as we need half angles
            val cr = cos(euler.roll * degToRad * 0.5f)
            val cp = cos(euler.pitch * degToRad * 0.5f)
            val cy = cos(euler.yaw * degToRad * 0.5f)
            val sr = sin(euler.roll * degToRad * 0.5f)
            val sp = sin(euler.pitch * degToRad * 0.5f)
            val sy = sin(euler.yaw * degToRad * 0.5f)

            result.w = cr * cp * cy + sr * sp * sy
            result.x = sr * cp * cy - cr * sp * sy
            result.y = cr * sp * cy + sr * cp * sy
            result.z = cr * cp * sy - sr * sp * cy
        }

        @JsName("fromEulerIp")
        fun fromEuler(euler: ZEuler): ZQuaternion {
            val result = ZQuaternion()
            fromEuler(result, euler)
            return result
        }

        /**
         * Creates a new quaternion from Euler angles.
         * @param roll Rotation around X axis
         * @param pitch Rotation around Y axis
         * @param yaw Rotation around Z axis
         * @return A new quaternion representing the rotation
         */
        @JsName("fromEulerWithValues")
        fun fromEuler(roll: Float, pitch: Float, yaw: Float): ZQuaternion {
            val result = ZQuaternion()
            val euler = ZEuler(roll, pitch, yaw)
            fromEuler(result, euler)
            return result
        }

        // Original slerp implementation
        // fun slerp(result: ZQuaternion, t: Float, q1: ZQuaternion, q2: ZQuaternion) {
        //     // Dot product
        //     var dot = q1.w * q2.w + q1.x * q2.x + q1.y * q2.y + q1.z * q2.z
        //     // Invert q2 if dot < 0 to take the shortest path
        //     var q3 = q2
        //     if (dot < 0f) {
        //       dot = -dot
        //       q3 = ZQuaternion(-q2.x, -q2.y, -q2.z, -q2.w)
        //     }
        //     // Close to 1, fallback to NLERP to avoid division by 0
        //     if (dot > 0.9995f) {
        //         val invT = 1f - t
        //         result.w = invT * q1.w + t * q3.w
        //         result.x = invT * q1.x + t * q3.x
        //         result.y = invT * q1.y + t * q3.y
        //         result.z = invT * q1.z + t * q3.z
        //         normalize(result, result)
        //         return
        //     }
        //     // Angle between quaternions
        //     val theta0 = acos(dot)
        //     val theta = theta0 * t
        //     val sinTheta = sin(theta)
        //     val sinTheta0 = sin(theta0)
        //     val s0 = sin(theta0-theta)/sinTheta0 // sin((1-t)*theta0)/sin(theta0)
        //     val s1 = sinTheta / sinTheta0 // sin(t*theta)/sin(theta0)

        //     result.w = q1.w * s0 + q3.w * s1
        //     result.x = q1.x * s0 + q3.x * s1
        //     result.y = q1.y * s0 + q3.y * s1
        //     result.z = q1.z * s0 + q3.z * s1
        //     normalize(result, result)
        // }

        @JsName("slerpIp")
        fun slerp(result: ZQuaternion, t: Float, qa: ZQuaternion, qb: ZQuaternion) {
            // Extracted from https://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/slerp/index.htm

            if ( abs(t) < 0.00001f ) {
                ZQuaternion.copy(result, qa)
                normalize(result, result)
                return
            }
            if ( abs(t - 1) < 0.00001f ) {
                ZQuaternion.copy(result, qb)
                normalize(result, result)
                return
            }

            // Calculate angle between them
            var cosHalfTheta = qa.w * qb.w + qa.x * qb.x + qa.y * qb.y + qa.z * qb.z

            var qc = ZQuaternion(qb.w, qb.x, qb.y, qb.z)
            if (cosHalfTheta < 0) {
                qc.w = -qb.w
                qc.x = -qb.x
                qc.y = -qb.y
                qc.z = -qb.z
                cosHalfTheta = -cosHalfTheta
            }

            // If qa=qb or qa=-qb then theta = 0 and we can return qa
            if (cosHalfTheta >= 1.0f) {
                result.w = qa.w
                result.x = qa.x
                result.y = qa.y
                result.z = qa.z
                normalize(result, result)
                return
            }

            // Calculate temporary values
            val sinHalfTheta = sqrt(1.0f - cosHalfTheta * cosHalfTheta)
            val halfTheta = atan2(sinHalfTheta, cosHalfTheta)

            // If theta = 180 degrees then result is not fully defined
            // We could rotate around any axis normal to qa or qb
            if (abs(sinHalfTheta) < 0.00001f) {
                result.w = qa.w * 0.5f + qc.w * 0.5f
                result.x = qa.x * 0.5f + qc.x * 0.5f
                result.y = qa.y * 0.5f + qc.y * 0.5f
                result.z = qa.z * 0.5f + qc.z * 0.5f
                normalize(result, result)
                return
            }

            val ratioA = sin((1.0f - t) * halfTheta) / sinHalfTheta
            val ratioB = sin(t * halfTheta) / sinHalfTheta

            // Calculate quaternion
            result.w = qa.w * ratioA + qc.w * ratioB
            result.x = qa.x * ratioA + qc.x * ratioB
            result.y = qa.y * ratioA + qc.y * ratioB
            result.z = qa.z * ratioA + qc.z * ratioB
            normalize(result, result)
        }

        fun slerp(t: Float, q1: ZQuaternion, q2: ZQuaternion): ZQuaternion {
            val result = ZQuaternion()
            slerp(result, t, q1, q2)
            return result
        }

    }
}

@Serializable
private data class ZQuaternionSurrogate(
    @ProtoNumber(1) val w: Float,
    @ProtoNumber(2) val x: Float,
    @ProtoNumber(3) val y: Float,
    @ProtoNumber(4) val z: Float,
)

internal class ZQuaternionSerializer: KSerializer<ZQuaternion> {
    override val descriptor: SerialDescriptor
        get() = ZQuaternionSurrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): ZQuaternion {
        val surrogate = decoder.decodeSerializableValue(ZQuaternionSurrogate.serializer())
        return ZQuaternion(surrogate.w, surrogate.x, surrogate.y, surrogate.z)
    }

    override fun serialize(encoder: Encoder, value: ZQuaternion) {
        val surrogate = ZQuaternionSurrogate(value.w, value.x, value.y, value.z)
        encoder.encodeSerializableValue(ZQuaternionSurrogate.serializer(), surrogate)
    }

}
