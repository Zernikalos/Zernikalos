package zernikalos

import kotlinx.serialization.SerialName
import kotlin.js.JsExport

@JsExport
enum class ZDataType {
    @SerialName("byte")
    BYTE,
    @SerialName("ubyte")
    UNSIGNED_BYTE,

    @SerialName("short")
    SHORT,
    @SerialName("ushort")
    UNSIGNED_SHORT,

    @SerialName("int")
    INT,
    @SerialName("uint")
    UNSIGNED_INT,

    @SerialName("float")
    FLOAT,
    @SerialName("double")
    DOUBLE,

    @SerialName("vec2")
    VEC2,
    @SerialName("vec3")
    VEC3,
    @SerialName("vec4")
    VEC4,

    @SerialName("mat2")
    MAT2F,
    @SerialName("mat3")
    MAT3F,
    @SerialName("mat4")
    MAT4F,
}