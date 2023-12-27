package zernikalos.math

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
sealed interface ZAlgebraObject {

    val values: FloatArray

    val size: Int

    val count: Int

}