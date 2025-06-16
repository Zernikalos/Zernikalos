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

@JsExport
@Serializable(with = ZColorSerializer::class)
class ZColor(): ZAlgebraObject {

    private val _values: FloatArray = floatArrayOf(0f, 0f, 0f, 0f)

    override val dataType: ZDataType
        get() = ZTypes.RGBA

    override val floatArray: FloatArray
        get() = _values

    override val byteArray: ByteArray
        get() = floatArray.toByteArray()

    override val size: Int
        get() = dataType.size

    override val count: Int
        get() = 1

    var red: Float
        get() = _values[0]
        set(value) {
            _values[0] = value
        }

    var green: Float
        get() = _values[1]
        set(value) {
            _values[1] = value
        }

    var blue: Float
        get() = _values[2]
        set(value) {
            _values[2] = value
        }

    var alpha: Float
        get() = _values[3]
        set(value) {
            _values[3] = value
        }

    var r: Float
        get() = red
        set(value) {
            red = value
        }

    var g: Float
        get() = green
        set(value) {
            green = value
        }

    var b: Float
        get() = blue
        set(value) {
            blue = value
        }

    var a: Float
        get() = alpha
        set(value) {
            alpha = value
        }

    @JsName("initWithValues")
    constructor(red: Float, green: Float, blue: Float, alpha: Float) : this() {
        colorFromFloats(red, green, blue, alpha)
    }

    @JsName("initWithByteValues")
    constructor(red: Int, green: Int, blue: Int, alpha: Int) : this() {
        colorFromBytes(red, green, blue, alpha)
    }

    // Refactored Constructor
    @JsName("initWithHex")
    constructor(hex: String) : this() {
        colorFromHex(hex)
    }

    fun colorFromFloats(red: Float, green: Float, blue: Float, alpha: Float) {
        require(alpha in 0f..1f) { "Alpha must be in the range 0.0-1.0" }
        require(red in 0f..1f) { "Red must be in the range 0.0-1.0" }
        require(green in 0f..1f) { "Green must be in the range 0.0-1.0" }
        require(blue in 0f..1f) { "Blue must be in the range 0.0-1.0" }
        this.red = red
        this.green = green
        this.blue = blue
        this.alpha = alpha
    }

    fun colorFromBytes(red: Int, green: Int, blue: Int, alpha: Int) {
        require(alpha in 0..255) { "Alpha must be in the range 0-255" }
        require(red in 0..255) { "Red must be in the range 0-255" }
        require(green in 0..255) { "Green must be in the range 0-255" }
        require(blue in 0..255) { "Blue must be in the range 0-255" }

        this.red = red / 255.0f
        this.green = green / 255.0f
        this.blue = blue / 255.0f
        this.alpha = alpha / 255.0f
    }

    fun colorFromHex(hex: String) {
        require(isValidHex(hex)) { "Invalid hex color string" }

        val cleanHex = hex.trim('#')
        val colorValue = cleanHex.toLong(16)
        if (cleanHex.length == 6) {
            setColorsRGB(colorValue)
            this.alpha = 1.0f
        } else {
            setColorsRGBA(colorValue)
        }
    }

    private fun isValidHex(hex: String) = hex.matches(Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$"))

    private fun setColorsRGB(colorValue: Long) {
        this.red = ((colorValue shr 16) and 0xFF) / 255.0f
        this.green = ((colorValue shr 8) and 0xFF) / 255.0f
        this.blue = (colorValue and 0xFF) / 255.0f
    }

    private fun setColorsRGBA(colorValue: Long) {
        this.red = ((colorValue shr 24) and 0xFF) / 255.0f
        this.green = ((colorValue shr 16) and 0xFF) / 255.0f
        this.blue = ((colorValue shr 8) and 0xFF) / 255.0f
        this.alpha = (colorValue and 0xFF) / 255.0f
    }
}

@Serializable
private data class ZColorSurrogate(
    @ProtoNumber(1)
    val red: Float,
    @ProtoNumber(2)
    val green: Float,
    @ProtoNumber(3)
    val blue: Float,
    @ProtoNumber(4)
    val alpha: Float,
)

private class ZColorSerializer: KSerializer<ZColor> {
    override val descriptor: SerialDescriptor
        get() = ZColorSurrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): ZColor {
        val surrogate = decoder.decodeSerializableValue(ZColorSurrogate.serializer())
        return ZColor(surrogate.red, surrogate.green, surrogate.blue, surrogate.alpha)
    }

    override fun serialize(encoder: Encoder, value: ZColor) {
        val surrogate = ZColorSurrogate(value.red, value.green, value.blue, value.alpha)
        encoder.encodeSerializableValue(ZColorSurrogate.serializer(), surrogate)
    }

}
