package zernikalos

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.js.JsExport

@JsExport
enum class ZBaseType {

    @ProtoNumber(0)
    BYTE,
    @ProtoNumber(1)
    UNSIGNED_BYTE,

    @ProtoNumber(2)
    SHORT,
    @ProtoNumber(3)
    UNSIGNED_SHORT,

    @ProtoNumber(4)
    INT,
    @ProtoNumber(5)
    UNSIGNED_INT,

    @ProtoNumber(6)
    FLOAT,
    @ProtoNumber(7)
    DOUBLE,

    @ProtoNumber(8)
    TEXTURE;

    val byteSize: Int
        get() {
            return when(this) {
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
    SCALAR,

    @ProtoNumber(1)
    VEC2,
    @ProtoNumber(2)
    VEC3,
    @ProtoNumber(3)
    VEC4,

    @ProtoNumber(4)
    MAT2,
    @ProtoNumber(5)
    MAT3,
    @ProtoNumber(6)
    MAT4,

    @ProtoNumber(7)
    TEXTURE
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
                ZFormatType.SCALAR -> 1
                ZFormatType.VEC2 -> 2
                ZFormatType.VEC3 -> 3
                ZFormatType.VEC4 -> 4
                ZFormatType.MAT2 -> 4
                ZFormatType.MAT3 -> 9
                ZFormatType.MAT4 -> 16
                ZFormatType.TEXTURE -> 0
            }
        }

    val byteSize: Int
        get() = size * type.byteSize
}

object ZTypes {
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
}
