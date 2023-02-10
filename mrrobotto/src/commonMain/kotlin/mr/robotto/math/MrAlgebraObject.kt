package mr.robotto.math

import kotlinx.serialization.Serializable

@Serializable
sealed interface MrAlgebraObject {

    val values: FloatArray

    val size: Int

}