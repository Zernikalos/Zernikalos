package zernikalos

import org.khronos.webgl.*
import org.w3c.dom.HTMLCanvasElement

abstract external class WebGLVertexArrayObject: WebGLObject

abstract external class WebGL2RenderingContext: WebGLRenderingContext {
    fun createVertexArray(): WebGLVertexArrayObject

    fun bindVertexArray(vao: WebGLVertexArrayObject)
}

@JsExport
actual class ZRenderingContext {

    private lateinit var gl: WebGL2RenderingContext

    fun setContext(canvas: HTMLCanvasElement) {
        val context = canvas.getContext("webgl2") ?: throw Error("Unable to get context")
        gl = context as WebGL2RenderingContext
    }

    actual fun enable(feat: Int) {
        gl.enable(feat)
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

    actual fun cullFace(mode: Int) {
        gl.cullFace(mode)
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

    actual fun bindAttribLocation(program: GLWrap, index: Int, attrName: String) {
        gl.bindAttribLocation(program.id as WebGLProgram, index, attrName)
    }

    actual fun getUniformLocation(program: GLWrap, uniformName: String): GLWrap {
        val id = gl.getUniformLocation(program.id as WebGLProgram, uniformName)
        return GLWrap(id)
    }

    actual fun uniformMatrix4fv(uniform: GLWrap, count: Int, transpose: Boolean, values: FloatArray) {
        gl.uniformMatrix4fv(uniform.id as WebGLUniformLocation, transpose, values.toTypedArray())
    }

    actual fun createBuffer(): GLWrap {
        val id = gl.createBuffer()
        return GLWrap(id)
    }

    actual fun bindBuffer(targetType: BufferTargetType, buffer: GLWrap) {
        gl.bindBuffer(targetType.value, buffer.id as WebGLBuffer)
    }

    actual fun bufferData(targetType: BufferTargetType, dataArray: ByteArray, usageType: BufferUsageType) {
        val arrayData: ArrayBuffer = Uint8Array(dataArray.toTypedArray()).buffer
        gl.bufferData(targetType.value, arrayData, usageType.value)
    }

    actual fun enableVertexAttrib(index: Int) {
        gl.enableVertexAttribArray(index)
    }

    actual fun enableVertexAttribArray(index: Int) {
        gl.enableVertexAttribArray(index)
    }

    actual fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) {
        gl.vertexAttribPointer(index, size, type, normalized, stride, offset)
    }

    actual fun createVertexArray(): GLWrap {
        val id = gl.createVertexArray()
        return GLWrap(id)
    }

    actual fun bindVertexArray(vao: GLWrap) {
        gl.bindVertexArray(vao.id as WebGLVertexArrayObject)
    }

    actual fun drawArrays(mode: Int, first: Int, count: Int) {
        gl.drawArrays(mode, first, count)
    }

    actual fun drawElements(mode: Int, count: Int, type: Int, offset: Int) {
        gl.drawElements(mode, count, type, offset)
    }

}

actual object ExpectEnabler {
    actual val DEPTH_TEST: Int = WebGLRenderingContext.DEPTH_TEST
}

actual object ExpectTypes {
    actual val UNSIGNED_BYTE: Int = WebGLRenderingContext.UNSIGNED_BYTE
    actual val BYTE: Int = WebGLRenderingContext.BYTE
    actual val INT: Int = WebGLRenderingContext.INT
    actual val UNSIGNED_INT: Int = WebGLRenderingContext.UNSIGNED_INT
    actual val SHORT: Int = WebGLRenderingContext.SHORT
    actual val UNSIGNED_SHORT: Int = WebGLRenderingContext.UNSIGNED_SHORT
    actual val FLOAT: Int = WebGLRenderingContext.FLOAT
    actual val DOUBLE: Int = WebGLRenderingContext.FLOAT
}

actual object ExpectBufferTargetType {
    actual val ARRAY_BUFFER: Int = WebGLRenderingContext.ARRAY_BUFFER
    actual val ELEMENT_ARRAY_BUFFER: Int = WebGLRenderingContext.ELEMENT_ARRAY_BUFFER
}

actual object ExpectBufferUsageType {
    actual val STATIC_DRAW: Int = WebGLRenderingContext.STATIC_DRAW
}

actual object ExpectBufferBit {
    actual val COLOR_BUFFER: Int = WebGLRenderingContext.COLOR_BUFFER_BIT
    actual val DEPTH_BUFFER: Int = WebGLRenderingContext.DEPTH_BUFFER_BIT
}

actual object ExpectShaderType {
    actual val VERTEX_SHADER: Int = WebGLRenderingContext.VERTEX_SHADER
    actual val FRAGMENT_SHADER: Int = WebGLRenderingContext.FRAGMENT_SHADER
}

actual object ExpectDrawModes {
    actual val TRIANGLES: Int = WebGLRenderingContext.TRIANGLES
    actual val LINES: Int = WebGLRenderingContext.LINES
}

actual object ExpectCullModeType {
    actual val FRONT: Int = WebGLRenderingContext.FRONT
    actual val BACK: Int = WebGLRenderingContext.BACK
    actual val FRONT_AND_BACK: Int = WebGLRenderingContext.FRONT_AND_BACK
}
