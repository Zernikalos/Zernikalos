/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.js.JsExport

@JsExport
enum class ZBaseType {
    @ProtoNumber(0)
    NONE,

    @ProtoNumber(1)
    BYTE,
    @ProtoNumber(2)
    UNSIGNED_BYTE,

    @ProtoNumber(3)
    SHORT,
    @ProtoNumber(4)
    UNSIGNED_SHORT,

    @ProtoNumber(5)
    INT,
    @ProtoNumber(6)
    UNSIGNED_INT,

    @ProtoNumber(7)
    FLOAT,
    @ProtoNumber(8)
    DOUBLE,

    @ProtoNumber(9)
    TEXTURE;

    val byteSize: Int
        get() {
            return when(this) {
                NONE -> 0
                BYTE -> Byte.SIZE_BYTES
                UNSIGNED_BYTE -> UByte.SIZE_BYTES
                SHORT -> Short.SIZE_BYTES
                UNSIGNED_SHORT -> UShort.SIZE_BYTES
                INT -> Int.SIZE_BYTES
                UNSIGNED_INT -> UInt.SIZE_BYTES
                FLOAT -> Float.SIZE_BYTES
                DOUBLE -> Double.SIZE_BYTES
                TEXTURE -> 0
            }
        }
}

@JsExport
enum class ZFormatType {
    @ProtoNumber(0)
    NONE,

    @ProtoNumber(1)
    SCALAR,

    @ProtoNumber(2)
    VEC2,
    @ProtoNumber(3)
    VEC3,
    @ProtoNumber(4)
    VEC4,

    @ProtoNumber(5)
    MAT2,
    @ProtoNumber(6)
    MAT3,
    @ProtoNumber(7)
    MAT4,

    @ProtoNumber(8)
    TEXTURE,
    @ProtoNumber(9)
    QUATERNION,
    @ProtoNumber(10)
    EULER,
    @ProtoNumber(11)
    RGBA
}

@JsExport
@Serializable
data class ZDataType(
    @ProtoNumber(1)
    val type: ZBaseType,
    @ProtoNumber(2)
    val format: ZFormatType
) {

    /**
     * Represents the size of the data type.
     *
     * The size is determined based on the format of the data type. The formats have the following sizes:
     * - NONE: 0
     * - SCALAR: 1
     * - VEC2: 2
     * - VEC3: 3
     * - VEC4: 4
     * - MAT2: 4
     * - MAT3: 9
     * - MAT4: 16
     * - TEXTURE: 0
     * - QUATERNION: 4
     */
    val size: Int
        get() {
            return when (format) {
                ZFormatType.NONE -> 0
                ZFormatType.SCALAR -> 1
                ZFormatType.VEC2 -> 2
                ZFormatType.VEC3 -> 3
                ZFormatType.VEC4 -> 4
                ZFormatType.MAT2 -> 4
                ZFormatType.MAT3 -> 9
                ZFormatType.MAT4 -> 16
                ZFormatType.TEXTURE -> 0
                ZFormatType.QUATERNION -> 4
                ZFormatType.EULER -> 3
                ZFormatType.RGBA -> 4
            }
        }

    /**
     * Represents the size of the data type in bytes.
     *
     * This variable is calculated by multiplying the [size] property of [ZDataType]
     * with the [byteSize] property of [type].
     */
    val byteSize: Int
        get() = size * type.byteSize
}

@JsExport
object ZTypes {
    val NONE = ZDataType(ZBaseType.NONE, ZFormatType.NONE)

    val BYTE = ZDataType(ZBaseType.BYTE, ZFormatType.SCALAR)
    val BYTE2 = ZDataType(ZBaseType.BYTE, ZFormatType.VEC2)
    val BYTE3 = ZDataType(ZBaseType.BYTE, ZFormatType.VEC3)
    val BYTE4 = ZDataType(ZBaseType.BYTE, ZFormatType.VEC4)

    val UBYTE = ZDataType(ZBaseType.UNSIGNED_BYTE, ZFormatType.SCALAR)
    val UBYTE2 = ZDataType(ZBaseType.UNSIGNED_BYTE, ZFormatType.VEC2)
    val UBYTE3 = ZDataType(ZBaseType.UNSIGNED_BYTE, ZFormatType.VEC3)
    val UBYTE4 = ZDataType(ZBaseType.UNSIGNED_BYTE, ZFormatType.VEC4)

    val SHORT = ZDataType(ZBaseType.SHORT, ZFormatType.SCALAR)
    val SHORT2 = ZDataType(ZBaseType.SHORT, ZFormatType.VEC2)
    val SHORT3 = ZDataType(ZBaseType.SHORT, ZFormatType.VEC3)
    val SHORT4 = ZDataType(ZBaseType.SHORT, ZFormatType.VEC4)

    val USHORT = ZDataType(ZBaseType.UNSIGNED_SHORT, ZFormatType.SCALAR)
    val USHORT2 = ZDataType(ZBaseType.UNSIGNED_SHORT, ZFormatType.VEC2)
    val USHORT3 = ZDataType(ZBaseType.UNSIGNED_SHORT, ZFormatType.VEC3)
    val USHORT4 = ZDataType(ZBaseType.UNSIGNED_SHORT, ZFormatType.VEC4)

    val INT = ZDataType(ZBaseType.INT, ZFormatType.SCALAR)
    val INT2 = ZDataType(ZBaseType.INT, ZFormatType.VEC2)
    val INT3 = ZDataType(ZBaseType.INT, ZFormatType.VEC3)
    val INT4 = ZDataType(ZBaseType.INT, ZFormatType.VEC4)

    val UINT = ZDataType(ZBaseType.UNSIGNED_INT, ZFormatType.SCALAR)
    val UINT2 = ZDataType(ZBaseType.UNSIGNED_INT, ZFormatType.VEC2)
    val UINT3 = ZDataType(ZBaseType.UNSIGNED_INT, ZFormatType.VEC3)
    val UINT4 = ZDataType(ZBaseType.UNSIGNED_INT, ZFormatType.VEC4)

    val FLOAT = ZDataType(ZBaseType.FLOAT, ZFormatType.SCALAR)
    val DOUBLE = ZDataType(ZBaseType.DOUBLE, ZFormatType.SCALAR)

    val VEC2F = ZDataType(ZBaseType.FLOAT, ZFormatType.VEC2)
    val VEC3F = ZDataType(ZBaseType.FLOAT, ZFormatType.VEC3)
    val VEC4F = ZDataType(ZBaseType.FLOAT, ZFormatType.VEC4)

    val MAT2F = ZDataType(ZBaseType.FLOAT, ZFormatType.MAT2)
    val MAT3F = ZDataType(ZBaseType.FLOAT, ZFormatType.MAT3)
    val MAT4F = ZDataType(ZBaseType.FLOAT, ZFormatType.MAT4)

    val TEXTURE = ZDataType(ZBaseType.TEXTURE, ZFormatType.TEXTURE)
    val QUATERNION = ZDataType(ZBaseType.FLOAT, ZFormatType.QUATERNION)
    val EULER = ZDataType(ZBaseType.FLOAT, ZFormatType.EULER)
    val RGBA = ZDataType(ZBaseType.FLOAT, ZFormatType.RGBA)
}
