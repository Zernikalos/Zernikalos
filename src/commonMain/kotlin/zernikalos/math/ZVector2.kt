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
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.abs
import kotlin.math.sqrt

@JsExport
@Serializable(with = ZVector2Serializer::class)
class ZVector2(): ZAlgebraObject {

    private val _values = floatArrayOf(0f, 0f)

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

    @JsName("initWithValues")
    constructor(x: Float = 0f, y: Float = 0f): this() {
        setValues(x, y)
    }

    @JsName("initWithValue")
    constructor(v: Float) : this(v, v)

    override val values: FloatArray
        get() = floatArrayOf(x, y)

    val norm2: Float
        get() = sqrt(dot(this, this))

    override val size: Int
        get() = 2

    override val count: Int
        get() = 1

    override val dataType: ZDataType
        get() = ZTypes.VEC2F

    val normalized: ZVector2
        get() {
            val result = ZVector2()
            normalize(result, this)
            return result
        }

    operator fun set(i: Int, value: Float) {
        when(i) {
            0 -> x = value
            1 -> y = value
            else -> throw Error("Out of bounds array access")
        }
    }

    fun setValues(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    operator fun plus(v: ZVector2): ZVector2 {
        val result = ZVector2()
        add(result, this, v)
        return result
    }

    operator fun minus(v: ZVector2): ZVector2 {
        val result = ZVector2()
        subtract(result, this, v)
        return result
    }

    operator fun times(v: ZVector2): Float {
        return dot(this, v)
    }

    @JsName("timesScalar")
    operator fun times(scalar: Float): ZVector2 {
        val result = ZVector2()
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

    fun copy(v: ZVector2) {
        copy(this, v)
    }

    override fun toString(): String {
        return "[$x, $y]"
    }

    companion object Op {

        val Zero: ZVector2
            get() = ZVector2()

        val Ones: ZVector2
            get() = ZVector2(1.0f)

        val Forward: ZVector2
            get() = ZVector2(1f, 0f)

        val Right: ZVector2
            get() = ZVector2(0f, 1f)

        val Up: ZVector2
            get() = ZVector2(0f, 0f)

        fun copy(result: ZVector2, v: ZVector2) {
            result.x = v.x
            result.y = v.y
        }

        fun zero(result: ZVector2) {
            result.x = 0.0f
            result.y = 0.0f
        }

        fun add(result: ZVector2, op1: ZVector2, op2: ZVector2) {
            result.x = op1.x + op2.x
            result.y = op1.y + op2.y
        }

        fun dot(op1: ZVector2, op2: ZVector2): Float {
            return op1.x * op2.x + op1.y * op2.y
        }

        fun norm2(v: ZVector2): Float {
            return sqrt(dot(v, v))
        }

        @JsName("norm2PerValue")
        fun norm2(x: Float, y: Float, z: Float): Float {
            return sqrt(x * x + y * y + z * z)
        }

        fun multScalar(result: ZVector2, scalar: Float, v: ZVector2) {
            result.x = scalar * v.x
            result.y = scalar * v.y
        }

        fun normalize(result: ZVector2, v: ZVector2) {
            copy(result, v)
            val norm2 = v.norm2
            if (abs(norm2) < 0.00000000001) {
                return
            }
            val invNorm = 1.0f / norm2
            result.multScalar(invNorm)
        }

        fun subtract(result: ZVector2, op1: ZVector2, op2: ZVector2) {
            result.x = op1.x - op2.x
            result.y = op1.y - op2.y
        }

        fun lerp(result: ZVector2, t: Float, op1: ZVector2, op2: ZVector2) {
            result.x = (1 - t) * op1.x + t * op2.x
            result.y = (1 - t) * op1.y + t * op2.y
        }

    }
}

@Serializable
private data class ZVector2Surrogate(
    @ProtoNumber(1) val x: Float,
    @ProtoNumber(2) val y: Float,
)

private class ZVector2Serializer: KSerializer<ZVector2> {
    override val descriptor: SerialDescriptor
        get() = ZVector2Surrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): ZVector2 {
        val surrogate = decoder.decodeSerializableValue(ZVector2Surrogate.serializer())
        return ZVector2(surrogate.x, surrogate.y)
    }

    override fun serialize(encoder: Encoder, value: ZVector2) {
        val surrogate = ZVector2Surrogate(value.x, value.y)
        encoder.encodeSerializableValue(ZVector2Surrogate.serializer(), surrogate)
    }

}