package zernikalos.math

import kotlinx.serialization.Serializable

@Serializable
sealed interface ZAlgebraObject {

    val values: FloatArray

    val size: Int

}