package zernikalos.uniformgenerator

import zernikalos.ZDataType
import zernikalos.math.ZAlgebraObject
import kotlin.js.JsExport

@JsExport
data class ZUniformValue(
    val type: ZDataType,
    val value: ZAlgebraObject
)