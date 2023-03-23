package zernikalos.math

import kotlinx.serialization.Serializable

@Serializable
sealed interface ZkAlgebraObject {

    val values: FloatArray

    val size: Int

}