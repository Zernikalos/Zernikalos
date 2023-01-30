package mr.robotto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MrTypes {
    @SerialName("scalar")
    SCALAR,

    @SerialName("vec2")
    VEC2,
    @SerialName("vec3")
    VEC3,
    @SerialName("vec4")
    VEC4,

    @SerialName("mat2")
    MAT2,
    @SerialName("mat3")
    MAT3,
    @SerialName("mat4")
    MAT4
}