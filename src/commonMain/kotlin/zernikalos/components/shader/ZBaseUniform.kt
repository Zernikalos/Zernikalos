package zernikalos.components.shader

import zernikalos.ZDataType
import zernikalos.math.ZAlgebraObject
import kotlin.js.JsExport

@JsExport
interface ZBaseUniform {
    /**
     * Represents the unique identifier for a `ZUniform` instance.
     * This ID is used to differentiate between different uniform components
     */
    val id: Int

    /**
     * This is the name within the shader source code
     */
    val uniformName: String

    var value: ZAlgebraObject
}