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
    QUATERNION
}

@JsExport
@Serializable
data class ZDataType(
    @ProtoNumber(1)
    val type: ZBaseType,
    @ProtoNumber(2)
    val format: ZFormatType
) {
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
            }
        }

    val byteSize: Int
        get() = size * type.byteSize
}

@JsExport
object ZTypes {
    val NONE = ZDataType(ZBaseType.NONE, ZFormatType.NONE)

    val BYTE = ZDataType(ZBaseType.BYTE, ZFormatType.SCALAR)
    val UBYTE = ZDataType(ZBaseType.UNSIGNED_BYTE, ZFormatType.SCALAR)
    val INT = ZDataType(ZBaseType.INT, ZFormatType.SCALAR)
    val UINT = ZDataType(ZBaseType.UNSIGNED_INT, ZFormatType.SCALAR)
    val SHORT = ZDataType(ZBaseType.SHORT, ZFormatType.SCALAR)
    val USHORT = ZDataType(ZBaseType.UNSIGNED_SHORT, ZFormatType.SCALAR)
    val FLOAT = ZDataType(ZBaseType.FLOAT, ZFormatType.SCALAR)
    val DOUBLE = ZDataType(ZBaseType.DOUBLE, ZFormatType.SCALAR)

    val VEC2F = ZDataType(ZBaseType.FLOAT, ZFormatType.VEC2)
    val VEC3F = ZDataType(ZBaseType.FLOAT, ZFormatType.VEC3)
    val VEC4F = ZDataType(ZBaseType.FLOAT, ZFormatType.VEC4)

    val MAT2F = ZDataType(ZBaseType.FLOAT, ZFormatType.MAT2)
    val MAT3F = ZDataType(ZBaseType.FLOAT, ZFormatType.MAT3)
    val MAT4F = ZDataType(ZBaseType.FLOAT, ZFormatType.MAT4)

    val TEXTURE = ZDataType(ZBaseType.TEXTURE, ZFormatType.TEXTURE)
    val QUATERNIONF = ZDataType(ZBaseType.FLOAT, ZFormatType.QUATERNION)
}
