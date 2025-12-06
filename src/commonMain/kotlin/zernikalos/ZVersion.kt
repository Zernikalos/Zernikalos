package zernikalos

import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("version")
object Version {
    const val VERSION = "0.6.0"
    const val ZKO_VERSION = zernikalos.loader.ZKO_VERSION
}
