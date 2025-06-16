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
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.utils.toByteArray
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.sqrt

@JsExport
@Serializable(with = ZVector4Serializer::class)
class ZVector4(): ZAlgebraObject {

    private val _values = FloatArray(4)

    var x: Float
        get() = _values[0]
        set(value) {
            _values[0] = value
        }

    var y: Float
        get() = _values[1]
        set(value) {
            _values[1] = value
        }

    var z: Float
        get() = _values[2]
        set(value) {
            _values[2] = value
        }

    var w: Float
        get() = _values[3]
        set(value) {
            _values[3] = value
        }

    @JsName("initWithValues")
    constructor(x: Float = 0f, y: Float = 0f, z: Float = 0f, w: Float = 0f): this() {
        setValues(x, y, z, w)
    }

    @JsName("initWithValue")
    constructor(v: Float): this(v, v, v, v)

    @JsName("initWithVec3")
    constructor(v: ZVector3) : this(v.x, v.y, v.z, 1.0f)

    override val floatArray: FloatArray
        get() = _values

    override val byteArray: ByteArray
        get() = floatArray.toByteArray()

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

    override fun toString(): String {
        return "[$w, $x, $y, $z]"
    }

    companion object Op {
        fun add(result: ZVector4, op1: ZVector4, op2: ZVector4) {
            result.x = op1.x + op2.x
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

        fun multMatrix(result: ZVector4, m: ZMatrix4, v: ZVector4) {
            val x = v.x
            val y = v.y
            val z = v.z
            val w = v.w
            result[0] = m[0 + 4 * 0] * x + m[0 + 4 * 1] * y + m[0 + 4 * 2] * z + m[0 + 4 * 3] * w
            result[1] = m[1 + 4 * 0] * x + m[1 + 4 * 1] * y + m[1 + 4 * 2] * z + m[1 + 4 * 3] * w
            result[2] = m[2 + 4 * 0] * x + m[2 + 4 * 1] * y + m[2 + 4 * 2] * z + m[2 + 4 * 3] * w
            result[3] = m[3 + 4 * 0] * x + m[3 + 4 * 1] * y + m[3 + 4 * 2] * z + m[3 + 4 * 3] * w
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

@Serializable
private data class ZVector4Surrogate(
    @ProtoNumber(1) val x: Float,
    @ProtoNumber(2) val y: Float,
    @ProtoNumber(3) val z: Float,
    @ProtoNumber(4) val w: Float,
)

private class ZVector4Serializer: KSerializer<ZVector4> {
    override val descriptor: SerialDescriptor
        get() = ZVector4Surrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): ZVector4 {
        val surrogate = decoder.decodeSerializableValue(ZVector4Surrogate.serializer())
        return ZVector4(surrogate.x, surrogate.y, surrogate.z, surrogate.w)
    }

    override fun serialize(encoder: Encoder, value: ZVector4) {
        val surrogate = ZVector4Surrogate(value.x, value.y, value.z, value.w)
        encoder.encodeSerializableValue(ZVector4Surrogate.serializer(), surrogate)
    }

}
