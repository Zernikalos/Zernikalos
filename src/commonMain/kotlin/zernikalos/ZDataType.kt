package zernikalos

import kotlinx.serialization.SerialName
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.js.JsExport

@JsExport
enum class ZDataType {
    @SerialName("byte")
    @ProtoNumber(0)
    BYTE,
    @SerialName("ubyte")
    @ProtoNumber(1)
    UNSIGNED_BYTE,

    @SerialName("short")
    @ProtoNumber(2)
    SHORT,
    @SerialName("ushort")
    @ProtoNumber(3)
    UNSIGNED_SHORT,

    @SerialName("int")
    @ProtoNumber(4)
    INT,
    @SerialName("uint")
    @ProtoNumber(5)
    UNSIGNED_INT,

    @SerialName("float")
    @ProtoNumber(6)
    FLOAT,
    @SerialName("double")
    @ProtoNumber(7)
    DOUBLE,

    @SerialName("vec2")
    @ProtoNumber(8)
    VEC2,
    @SerialName("vec3")
    @ProtoNumber(9)
    VEC3,
    @SerialName("vec4")
    @ProtoNumber(10)
    VEC4,

    @SerialName("mat2")
    @ProtoNumber(11)
    MAT2F,
    @SerialName("mat3")
    @ProtoNumber(12)
    MAT3F,
    @SerialName("mat4")
    @ProtoNumber(13)
    MAT4F,

    @SerialName("texture")
    @ProtoNumber(14)
    TEXTURE
}