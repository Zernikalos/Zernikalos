package zernikalos.components.mesh

import kotlin.js.JsExport

@JsExport
enum class ZDrawMode {
    POINTS,
    LINES,
    TRIANGLES,
    LINE_STRIP,
    TRIANGLE_STRIP
}