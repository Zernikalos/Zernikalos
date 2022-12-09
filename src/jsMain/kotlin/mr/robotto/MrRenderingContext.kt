package mr.robotto

import kotlinx.serialization.SerialName
import org.khronos.webgl.WebGLProgram
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLRenderingContextBase
import org.khronos.webgl.WebGLShader
import org.w3c.dom.HTMLCanvasElement

@OptIn(ExperimentalJsExport::class)
@JsExport
actual class MrRenderingContext {

    private lateinit var gl: WebGLRenderingContextBase

    fun setContext(canvas: HTMLCanvasElement) {
        val context = canvas.getContext("webgl2") ?: throw Error("Unable to get context")
        gl = context as WebGLRenderingContextBase
    }

    actual fun clearColor(r: Float, g: Float, b: Float, a: Float) {
        gl.clearColor(r, g, b, a)
    }

    actual fun viewport(top: Int, left: Int, width: Int, height: Int) {
        gl.viewport(top, left, width, height)
    }

    actual fun clear(buffer: Int) {
        gl.clear(buffer)
    }

    actual fun createProgram(): GLWrap {
        val id = gl.createProgram()
        return GLWrap(id)
    }

    actual fun useProgram(program: GLWrap) {
        gl.useProgram(program.id as WebGLProgram)
    }

    actual fun linkProgram(program: GLWrap) {
        gl.linkProgram(program.id as WebGLProgram)
    }

    actual fun createShader(shaderType: Int): GLWrap {
        val id = gl.createShader(shaderType)
        return GLWrap(id)
    }

    actual fun deleteShader(shader: GLWrap) {
        gl.deleteShader(shader.id as WebGLShader)
    }

    actual fun shaderSource(shader: GLWrap, source: String) {
        gl.shaderSource(shader.id as WebGLShader, source)
    }

    actual fun compileShader(shader: GLWrap) {
        gl.compileShader(shader.id as WebGLShader)
    }

    actual fun attachShader(program: GLWrap, shader: GLWrap) {
        gl.attachShader(program.id as WebGLProgram, shader.id as WebGLShader)
    }

    actual fun getError(): Int {
        return gl.getError()
    }

    actual fun getShaderInfoLog(shader: GLWrap): String {
        return gl.getShaderInfoLog(shader.id as WebGLShader) ?: ""
    }

}

actual object BufferBit {
    actual val COLOR_BUFFER: Int = WebGLRenderingContext.COLOR_BUFFER_BIT
    actual val DEPTH_BUFFER: Int = WebGLRenderingContext.DEPTH_BUFFER_BIT
}

actual object ShaderType {
    actual val VERTEX_SHADER: Int = WebGLRenderingContext.VERTEX_SHADER
    actual val FRAGMENT_SHADER: Int = WebGLRenderingContext.FRAGMENT_SHADER
}
