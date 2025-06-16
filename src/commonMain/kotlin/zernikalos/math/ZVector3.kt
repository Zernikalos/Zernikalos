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
import kotlin.math.abs
import kotlin.math.sqrt

@JsExport
@Serializable(with = ZVector3Serializer::class)
class ZVector3(): ZAlgebraObject {

    private val _values = floatArrayOf(0f, 0f, 0f)

    var x: Float
        get() {
            return _values[0]
        }
        set(value) {
            _values[0] = value
        }

    var y: Float
        get() {
            return _values[1]
        }
        set(value) {
            _values[1] = value
        }

    var z: Float
        get() {
            return _values[2]
        }
        set(value) {
            _values[2] = value
        }

    @JsName("initWithValues")
    constructor(x: Float = 0f, y: Float = 0f, z: Float = 0f): this() {
        setValues(x, y ,z)
    }

    @JsName("initWithValue")
    constructor(v: Float) : this(v, v, v)

    @JsName("initWithVec4")
    constructor(v4: ZVector4) : this() {
        fromVec4(this, v4)
    }

    override val floatArray: FloatArray
        get() = floatArrayOf(x, y, z)

    override val byteArray: ByteArray
        get() = floatArray.toByteArray()

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

    fun add(v: ZVector3) {
        add(this, this, v)
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

    fun rotate(q: ZQuaternion) {
        rotateVector(this, q, this)
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
            result.x = op1.x + op2.x
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

        fun multMatrix(result: ZVector3, m: ZMatrix4, v: ZVector3) {
            val x = v.x
            val y = v.y
            val z = v.z
            val w = 1.0f
            result[0] = m[0 + 4 * 0] * x + m[0 + 4 * 1] * y + m[0 + 4 * 2] * z + m[0 + 4 * 3] * w
            result[1] = m[1 + 4 * 0] * x + m[1 + 4 * 1] * y + m[1 + 4 * 2] * z + m[1 + 4 * 3] * w
            result[2] = m[2 + 4 * 0] * x + m[2 + 4 * 1] * y + m[2 + 4 * 2] * z + m[2 + 4 * 3] * w
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

        @JsName("lerpIp")
        fun lerp(result: ZVector3, t: Float, op1: ZVector3, op2: ZVector3) {
            result.x = (1 - t) * op1.x + t * op2.x
            result.y = (1 - t) * op1.y + t * op2.y
            result.z = (1 - t) * op1.z + t * op2.z
        }

        fun lerp(t: Float, op1: ZVector3, op2: ZVector3): ZVector3 {
            val result = ZVector3()
            lerp(result, t, op1, op2)
            return result
        }

        fun rotateVector(result: ZVector3, quat: ZQuaternion, v: ZVector3) {
            val Qv = ZQuaternion()
            val q = ZQuaternion()

            //Qv = Q(0,vx,vy,vz)
            ZQuaternion.fromVec3(Qv, v)
            //q=q/||q||
            ZQuaternion.normalize(q, quat)

            //q*Qv
            ZQuaternion.mult(Qv, q, Qv)
            //q^
            ZQuaternion.conjugate(q, q)
            //q*Qv*q^
            ZQuaternion.mult(Qv, Qv, q)
            result.setValues(Qv.x, Qv.y, Qv.z)
        }

    }
}

@Serializable
private data class ZVector3Surrogate(
    @ProtoNumber(1) val x: Float,
    @ProtoNumber(2) val y: Float,
    @ProtoNumber(3) val z: Float,
)

private class ZVector3Serializer: KSerializer<ZVector3> {
    override val descriptor: SerialDescriptor
        get() = ZVector3Surrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): ZVector3 {
        val surrogate = decoder.decodeSerializableValue(ZVector3Surrogate.serializer())
        return ZVector3(surrogate.x, surrogate.y, surrogate.z)
    }

    override fun serialize(encoder: Encoder, value: ZVector3) {
        val surrogate = ZVector3Surrogate(value.x, value.y, value.z)
        encoder.encodeSerializableValue(ZVector3Surrogate.serializer(), surrogate)
    }

}
