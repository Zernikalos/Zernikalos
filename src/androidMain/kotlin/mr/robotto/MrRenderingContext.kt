package mr.robotto

import android.opengl.GLES30

actual class MrRenderingContext {

    actual fun clearColor(r: Float, g: Float, b: Float, a: Float) {
        GLES30.glClearColor(r,g, b, a)
    }

    actual fun viewport(top: Int, left: Int, width: Int, height: Int) {
        GLES30.glViewport(top, left, width, height)
    }

    actual fun clear(buffer: Int) {
        GLES30.glClear(buffer)
    }

    actual fun createProgram(): GLWrap {
        val id = GLES30.glCreateProgram()
        return GLWrap(id)
    }

    actual fun useProgram(program: GLWrap) {
        GLES30.glUseProgram(program.id as Int)
    }

    actual fun linkProgram(program: GLWrap) {
        GLES30.glLinkProgram(program.id as Int)
    }

    actual fun createShader(shaderType: Int): GLWrap {
        val id = GLES30.glCreateShader(shaderType)
        return GLWrap(id)
    }

    actual fun deleteShader(shader: GLWrap) {
        GLES30.glDeleteShader(shader.id as Int)
    }

    actual fun shaderSource(shader: GLWrap, source: String) {
        GLES30.glShaderSource(shader.id as Int, source)
    }

    actual fun compileShader(shader: GLWrap) {
        GLES30.glCompileShader(shader.id as Int)
    }

    actual fun attachShader(program: GLWrap, shader: GLWrap) {
        GLES30.glAttachShader(program.id as Int, shader.id as Int)
    }

    actual fun getError(): Int {
        return GLES30.glGetError()
    }

    actual fun getShaderInfoLog(shader: GLWrap): String {
        return GLES30.glGetShaderInfoLog(shader.id as Int)
    }

}

actual object BufferBit {
    actual val COLOR_BUFFER: Int = GLES30.GL_COLOR_BUFFER_BIT
    actual val DEPTH_BUFFER: Int = GLES30.GL_DEPTH_BUFFER_BIT
}

actual object ShaderType {
    actual val VERTEX_SHADER: Int = GLES30.GL_VERTEX_SHADER
    actual val FRAGMENT_SHADER: Int = GLES30.GL_FRAGMENT_SHADER
}
