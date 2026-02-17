package zernikalos.utils

import kotlinx.serialization.Serializable

@Serializable
data class ZSemVer(
    val major: Int,
    val minor: Int,
    val patch: Int
) {
    companion object {
        fun parse(version: String): ZSemVer {
            val parts = version.split(".")
            require(parts.size == 3) {
                "Invalid version format. Must be in format X.Y.Z"
            }

            return try {
                ZSemVer(
                    major = parts[0].toInt(),
                    minor = parts[1].toInt(),
                    patch = parts[2].toInt()
                )
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Version components must be valid integers")
            }
        }
    }

    fun isCompatibleWith(other: ZSemVer): Boolean =
        major == other.major && minor == other.minor

    override fun toString(): String = "$major.$minor.$patch"
}
