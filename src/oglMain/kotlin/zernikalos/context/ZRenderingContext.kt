package zernikalos.context

import kotlinx.serialization.SerialName
import zernikalos.components.material.ZBitmap
import zernikalos.ui.ZSurfaceView
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

expect class ZGLRenderingContext(surfaceView: ZSurfaceView): ZRenderingContext {

    /** Commons **/

    fun enable(feat: Int)

    /** Viewport **/

    fun clearColor(r: Float, g: Float, b: Float, a: Float)

    fun viewport(top: Int, left: Int, width: Int, height: Int)

    fun clear(buffer: Int)

    fun cullFace(mode: Int)

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

    /** UNIFORMS **/

    fun getUniformLocation(program: GLWrap, uniformName: String): GLWrap

    fun uniformMatrix4fv(uniform: GLWrap, count: Int, transpose: Boolean, values: FloatArray)

    /** VBO **/

    fun createBuffer(): GLWrap

    fun bindBuffer(targetType: BufferTargetType, buffer: GLWrap)

    fun bufferData(targetType: BufferTargetType, dataArray: ByteArray, usageType: BufferUsageType)

    fun enableVertexAttrib(index: Int)

    fun enableVertexAttribArray(index: Int)

    fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int)

    /** VAO **/

    fun createVertexArray(): GLWrap

    fun bindVertexArray(vao: GLWrap)

    /** Draw **/

    fun drawArrays(mode: Int, first: Int, count: Int)

    fun drawElements(mode: Int, count: Int, type: Int, offset: Int)

    /** Textures **/

    fun genTexture(): GLWrap

    fun activeTexture()

    fun bindTexture(texture: GLWrap)

    fun texParameterMin()

    fun texParameterMag()

    fun texImage2D(bitmap: ZBitmap)

}

@OptIn(ExperimentalJsExport::class)
@JsExport
data class GLWrap(val id: Any? = null)

enum class BufferTargetType(val value: Int) {
    @SerialName("array")
    ARRAY_BUFFER(ExpectBufferTargetType.ARRAY_BUFFER),
    @SerialName("element")
    ELEMENT_ARRAY_BUFFER(ExpectBufferTargetType.ELEMENT_ARRAY_BUFFER)
}

expect object ExpectBufferTargetType {
    val ARRAY_BUFFER: Int
    val ELEMENT_ARRAY_BUFFER: Int
}