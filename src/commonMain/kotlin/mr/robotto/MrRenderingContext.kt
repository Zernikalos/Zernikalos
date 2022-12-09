package mr.robotto

import kotlinx.serialization.SerialName
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

expect class MrRenderingContext {

    fun clearColor(r: Float, g: Float, b: Float, a: Float)

    fun viewport(top: Int, left: Int, width: Int, height: Int)

    fun clear(buffer: Int)

    fun createProgram(): GLWrap

    fun useProgram(program: GLWrap)

    fun linkProgram(program: GLWrap)

    fun createShader(shaderType: Int): GLWrap

    fun deleteShader(shader: GLWrap)

    fun shaderSource(shader: GLWrap, source: String)

    fun compileShader(shader: GLWrap)

    fun attachShader(program: GLWrap, shader: GLWrap)

    fun getError(): Int

    fun getShaderInfoLog(shader: GLWrap): String

}

@OptIn(ExperimentalJsExport::class)
@JsExport
data class GLWrap(val id: Any? = null)

expect object BufferBit {
    val COLOR_BUFFER: Int
    val DEPTH_BUFFER: Int
}

expect object ShaderType {
    @SerialName("vertex")
    val VERTEX_SHADER: Int

    @SerialName("fragment")
    val FRAGMENT_SHADER: Int
}