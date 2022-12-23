package mr.robotto

import kotlinx.serialization.SerialName
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

expect class MrRenderingContext {

    /** Viewport **/

    fun clearColor(r: Float, g: Float, b: Float, a: Float)

    fun viewport(top: Int, left: Int, width: Int, height: Int)

    fun clear(buffer: Int)

    /** ShaderProgram **/

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

    fun bindAttribLocation(program: GLWrap, index: Int, attrName: String)

    /** VBO **/

    fun createBuffer(): GLWrap

    fun bindBuffer(targetType: BufferTargetType, buffer: GLWrap)

    fun bufferData(targetType: BufferTargetType, dataArray: ByteArray, usageType: BufferUsageType)

    fun enableVertexAttrib(index: Int)

    fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int)

    /** VAO **/

    fun createVertexArray(): GLWrap

    fun bindVertexArray(vao: GLWrap)

    /** Draw **/

    fun drawArrays(mode: Int, first: Int, count: Int)

}

@OptIn(ExperimentalJsExport::class)
@JsExport
data class GLWrap(val id: Any? = null)

enum class Types(val value: Int) {
    FLOAT(ExpectTypes.FLOAT)
}

enum class BufferTargetType(val value: Int) {
    ARRAY_BUFFER(ExpectBufferTargetType.ARRAY_BUFFER),
    ELEMENT_ARRAY_BUFFER(ExpectBufferTargetType.ELEMENT_ARRAY_BUFFER)
}

enum class BufferUsageType(val value: Int) {
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

enum class DrawModes(val value: Int) {
    TRIANGLES(ExpectDrawModes.TRIANGLES)
}

expect object ExpectTypes {
    val FLOAT: Int
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
}