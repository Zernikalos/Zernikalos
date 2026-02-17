package zernikalos

import org.lwjgl.opengl.*
import java.nio.ByteBuffer

actual class ZRenderingContext {

    actual fun enable(feat: Int) {
        GL11.glEnable(feat)
    }

    /** Viewport **/
    actual fun clearColor(r: Float, g: Float, b: Float, a: Float) {
        GL11.glClearColor(r, g, b, a)
    }

    actual fun viewport(top: Int, left: Int, width: Int, height: Int) {
        GL11.glViewport(top, left, width, height)
    }

    actual fun clear(buffer: Int) {
        GL11.glClear(buffer)
    }

    actual fun cullFace(mode: Int) {
        GL11.glCullFace(mode)
    }

    /** ShaderProgram **/
    actual fun createProgram(): GLWrap {
        val id = GL20.glCreateProgram()
        return GLWrap(id)
    }

    actual fun useProgram(program: GLWrap) {
        GL20.glUseProgram(program.id as Int)
    }

    actual fun linkProgram(program: GLWrap) {
        GL20.glLinkProgram(program.id as Int)
    }

    actual fun createShader(shaderType: Int): GLWrap {
        val id = GL20.glCreateShader(shaderType)
        return GLWrap(id)
    }

    actual fun deleteShader(shader: GLWrap) {
        GL20.glDeleteShader(shader.id as Int)
    }

    actual fun shaderSource(shader: GLWrap, source: String) {
        GL20.glShaderSource(shader.id as Int, source)
    }

    actual fun compileShader(shader: GLWrap) {
        GL20.glCompileShader(shader.id as Int)
    }

    actual fun attachShader(program: GLWrap, shader: GLWrap) {
        GL20.glAttachShader(program.id as Int, shader.id as Int)
    }

    actual fun getError(): Int {
        return GL11.glGetError()
    }

    actual fun getShaderInfoLog(shader: GLWrap): String {
        return GL20.glGetShaderInfoLog(shader.id as Int)
    }

    actual fun bindAttribLocation(program: GLWrap, index: Int, attrName: String) {
        GL20.glBindAttribLocation(program.id as Int, index, attrName)
    }

    /** UNIFORMS **/
    actual fun getUniformLocation(program: GLWrap, uniformName: String): GLWrap {
        val id = GL20.glGetUniformLocation(program.id as Int, uniformName)
        return GLWrap(id)
    }

    actual fun uniformMatrix4fv(
        uniform: GLWrap,
        count: Int,
        transpose: Boolean,
        values: FloatArray
    ) {
        GL20.glUniformMatrix4fv(uniform.id as Int, transpose, values)
    }

    /** VBO **/
    actual fun createBuffer(): GLWrap {
        val id = GL30.glGenVertexArrays()
        return GLWrap(id)
    }

    actual fun bindBuffer(targetType: BufferTargetType, buffer: GLWrap) {
        GL15.glBindBuffer(targetType.value, buffer.id as Int)
    }

    actual fun bufferData(
        targetType: BufferTargetType,
        dataArray: ByteArray,
        usageType: BufferUsageType
    ) {
        val buffer = ByteBuffer.wrap(dataArray)
        GL15.glBufferData(targetType.value, buffer, usageType.value)
    }

    actual fun enableVertexAttrib(index: Int) {
        GL20.glEnableVertexAttribArray(index)
    }

    actual fun vertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        offset: Int
    ) {
        GL20.glVertexAttribPointer(index, size, type, normalized, stride, offset.toLong())
    }

    /** VAO **/
    actual fun createVertexArray(): GLWrap {
        val id = GL30.glGenVertexArrays()
        return GLWrap(id)
    }

    actual fun bindVertexArray(vao: GLWrap) {
        GL30.glBindVertexArray(vao.id as Int)
    }

    /** Draw **/
    actual fun drawArrays(mode: Int, first: Int, count: Int) {
        GL11.glDrawArrays(mode, first, count)
    }

    actual fun drawElements(mode: Int, count: Int, type: Int, offset: Int) {
        GL30.glDrawElements(mode, count, type, offset.toLong())
    }

    actual fun enableVertexAttribArray(index: Int) {
        GL30.glEnableVertexAttribArray(index)
    }
}

actual object ExpectEnabler {
    actual val DEPTH_TEST: Int = GL11.GL_DEPTH_TEST
}

actual object ExpectTypes {
    actual val BYTE: Int = GL11.GL_BYTE
    actual val UNSIGNED_BYTE: Int = GL11.GL_UNSIGNED_BYTE
    actual val INT: Int =GL11.GL_INT
    actual val UNSIGNED_INT: Int = GL11.GL_UNSIGNED_INT
    actual val SHORT: Int = GL11.GL_SHORT
    actual val UNSIGNED_SHORT: Int = GL11.GL_UNSIGNED_SHORT
    actual val FLOAT: Int = GL11.GL_FLOAT
}

actual object ExpectBufferBit {
    actual val COLOR_BUFFER: Int = GL11.GL_COLOR_BUFFER_BIT
    actual val DEPTH_BUFFER: Int = GL11.GL_DEPTH_BUFFER_BIT
}

actual object ExpectShaderType {
    actual val VERTEX_SHADER: Int = GL20.GL_VERTEX_SHADER
    actual val FRAGMENT_SHADER: Int = GL20.GL_FRAGMENT_SHADER
}

actual object ExpectBufferTargetType {
    actual val ARRAY_BUFFER: Int = GL15.GL_ARRAY_BUFFER
    actual val ELEMENT_ARRAY_BUFFER: Int = GL15.GL_ELEMENT_ARRAY_BUFFER
}

actual object ExpectBufferUsageType {
    actual val STATIC_DRAW: Int = GL15.GL_STATIC_DRAW
}

actual object ExpectDrawModes {
    actual val TRIANGLES: Int = GL11.GL_TRIANGLES
    actual val LINES: Int = GL11.GL_LINES
}

actual object ExpectCullModeType {
    actual val FRONT: Int = GL11.GL_FRONT
    actual val BACK: Int = GL11.GL_BACK
    actual val FRONT_AND_BACK: Int = GL11.GL_FRONT_AND_BACK
}