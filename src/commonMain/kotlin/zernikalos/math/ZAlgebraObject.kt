package zernikalos.math

import kotlinx.serialization.Serializable
import zernikalos.ZDataType
import kotlin.js.JsExport

@JsExport
@Serializable
sealed interface ZAlgebraObject {

    val dataType: ZDataType

    val values: FloatArray

    val size: Int

    val count: Int

}