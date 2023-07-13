package zernikalos

import kotlinx.serialization.SerialName
import zernikalos.ui.ZSurfaceView
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

interface ZRenderingContext {

    fun initWithSurfaceView(surfaceView: ZSurfaceView)

}

enum class Enabler(val value: Int) {
    DEPTH_TEST(ExpectEnabler.DEPTH_TEST)
}

enum class Types(val value: Int) {
    BYTE(ExpectTypes.BYTE),
    UNSIGNED_BYTE(ExpectTypes.UNSIGNED_BYTE),
    SHORT(ExpectTypes.SHORT),
    UNSIGNED_SHORT(ExpectTypes.UNSIGNED_SHORT),
    INT(ExpectTypes.INT),
    UNSIGNED_INT(ExpectTypes.UNSIGNED_INT),
    FLOAT(ExpectTypes.FLOAT),
    DOUBLE(ExpectTypes.DOUBLE)
}

enum class BufferTargetType(val value: Int) {
    @SerialName("array")
    ARRAY_BUFFER(ExpectBufferTargetType.ARRAY_BUFFER),
    @SerialName("element")
    ELEMENT_ARRAY_BUFFER(ExpectBufferTargetType.ELEMENT_ARRAY_BUFFER)
}

enum class BufferUsageType(val value: Int) {
    @SerialName("static-draw")
    STATIC_DRAW(ExpectBufferUsageType.STATIC_DRAW)
}

enum class BufferBit(val value: Int) {
    COLOR_BUFFER(ExpectBufferBit.COLOR_BUFFER),
    DEPTH_BUFFER(ExpectBufferBit.DEPTH_BUFFER)
}

enum class ShaderType(val value: Int) {
    @SerialName("vertex")
    VERTEX_SHADER(ExpectShaderType.VERTEX_SHADER),
    @SerialName("fragment")
    FRAGMENT_SHADER(ExpectShaderType.FRAGMENT_SHADER)
}

enum class CullModeType(val value: Int) {
    FRONT(ExpectCullModeType.FRONT),
    BACK(ExpectCullModeType.BACK),
    FRONT_AND_BACK(ExpectCullModeType.FRONT_AND_BACK)
}

enum class DrawModes(val value: Int) {
    TRIANGLES(ExpectDrawModes.TRIANGLES),
    LINES(ExpectDrawModes.LINES)
}

expect object ExpectEnabler {
    val DEPTH_TEST: Int
}

expect object ExpectTypes {
    val BYTE: Int
    val UNSIGNED_BYTE: Int
    val INT: Int
    val UNSIGNED_INT: Int
    val SHORT: Int
    val UNSIGNED_SHORT: Int
    val FLOAT: Int
    val DOUBLE: Int
}

expect object ExpectBufferBit {
    val COLOR_BUFFER: Int
    val DEPTH_BUFFER: Int
}

expect object ExpectShaderType {
    val VERTEX_SHADER: Int
    val FRAGMENT_SHADER: Int
}

expect object ExpectBufferTargetType {
    val ARRAY_BUFFER: Int
    val ELEMENT_ARRAY_BUFFER: Int
}

expect object ExpectBufferUsageType {
    val STATIC_DRAW: Int
}

expect object ExpectDrawModes {
    val TRIANGLES: Int
    val LINES: Int
}

expect object ExpectCullModeType {
    val FRONT: Int
    val BACK: Int
    val FRONT_AND_BACK: Int
}