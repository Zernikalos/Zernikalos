package zernikalos.math

import zernikalos.ZDataType
import kotlin.js.JsExport

@JsExport
interface ZAlgebraObject {

    val dataType: ZDataType

    val values: FloatArray

    val size: Int

    val count: Int

}