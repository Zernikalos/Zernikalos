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

    fun createBuffer(): GLWrap

    fun bindBuffer(targetType: BufferTargetType, buffer: GLWrap)

    fun bufferData(targetType: BufferTargetType, dataArray: ByteArray, usageType: BufferUsageType)

    fun enableVertexAttrib(index: Int)

    fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int)

    fun createVertexArray(): GLWrap

    fun bindVertexArray(vao: GLWrap)

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

enum class BufferTargetType(val value: Int) {
    ARRAY_BUFFER(ExpectBufferTargetType.ARRAY_BUFFER),
    ELEMENT_ARRAY_BUFFER(ExpectBufferTargetType.ELEMENT_ARRAY_BUFFER)
}

expect object ExpectBufferTargetType {
    val ARRAY_BUFFER: Int
    val ELEMENT_ARRAY_BUFFER: Int
}

enum class BufferUsageType(val value: Int) {
    STATIC_DRAW(ExpectBufferUsageType.STATIC_DRAW)
}

expect object ExpectBufferUsageType {
    val STATIC_DRAW: Int
}