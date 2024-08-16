package zernikalos.context

import zernikalos.components.material.ZBitmap
import zernikalos.ui.ZSurfaceView

actual class ZGLRenderingContext actual constructor(surfaceView: ZSurfaceView) : ZRenderingContext {
    actual override fun initWithSurfaceView(surfaceView: ZSurfaceView) {
    }

    /** Commons **/
    actual fun enable(feat: Int) {
    }

    /** Viewport **/
    actual fun clearColor(r: Float, g: Float, b: Float, a: Float) {
    }

    actual fun viewport(top: Int, left: Int, width: Int, height: Int) {
    }

    actual fun clear(buffer: Int) {
    }

    actual fun cullFace(mode: Int) {
    }

    /** ShaderProgram **/
    actual fun createProgram(): GLWrap {
        TODO("Not yet implemented")
    }

    actual fun useProgram(program: GLWrap) {
    }

    actual fun linkProgram(program: GLWrap) {
    }

    actual fun createShader(shaderType: Int): GLWrap {
        TODO("Not yet implemented")
    }

    actual fun deleteShader(shader: GLWrap) {
    }

    actual fun shaderSource(shader: GLWrap, source: String) {
    }

    actual fun compileShader(shader: GLWrap) {
    }

    actual fun attachShader(program: GLWrap, shader: GLWrap) {
    }

    actual fun getError(): Int {
        TODO("Not yet implemented")
    }

    actual fun getShaderInfoLog(shader: GLWrap): String {
        TODO("Not yet implemented")
    }

    actual fun bindAttribLocation(program: GLWrap, index: Int, attrName: String) {
    }

    actual fun getAttribLocation(program: GLWrap, attrName: String): Int {
        TODO("Not yet implemented")
    }

    /** UNIFORMS **/
    actual fun getUniformLocation(
        program: GLWrap,
        uniformName: String
    ): GLWrap {
        TODO("Not yet implemented")
    }

    actual fun uniformMatrix4fv(
        uniform: GLWrap,
        count: Int,
        transpose: Boolean,
        values: FloatArray
    ) {
    }

    /** VBO **/
    actual fun createBuffer(): GLWrap {
        TODO("Not yet implemented")
    }

    actual fun bindBuffer(targetType: BufferTargetType, buffer: GLWrap) {
    }

    actual fun bufferData(
        targetType: BufferTargetType,
        dataArray: ByteArray,
        usageType: BufferUsageType
    ) {
    }

    actual fun enableVertexAttrib(index: Int) {
    }

    actual fun enableVertexAttribArray(index: Int) {
    }

    actual fun vertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        offset: Int
    ) {
    }

    /** VAO **/
    actual fun createVertexArray(): GLWrap {
        TODO("Not yet implemented")
    }

    actual fun bindVertexArray(vao: GLWrap) {
    }

    /** Draw **/
    actual fun drawArrays(mode: Int, first: Int, count: Int) {
    }

    actual fun drawElements(mode: Int, count: Int, type: Int, offset: Int) {
    }

    /** Textures **/
    actual fun genTexture(): GLWrap {
        TODO("Not yet implemented")
    }

    actual fun activeTexture() {
    }

    actual fun bindTexture(texture: GLWrap) {
    }

    actual fun texParameterMin() {
    }

    actual fun texParameterMag() {
    }

    actual fun texImage2D(bitmap: ZBitmap) {
    }

}

actual object ExpectBufferTargetType {
    actual val ARRAY_BUFFER: Int
        get() = TODO("Not yet implemented")
    actual val ELEMENT_ARRAY_BUFFER: Int
        get() = TODO("Not yet implemented")
}

actual object ExpectEnabler {
    actual val DEPTH_TEST: Int
        get() = TODO("Not yet implemented")
}

actual object ExpectBufferBit {
    actual val COLOR_BUFFER: Int
        get() = TODO("Not yet implemented")
    actual val DEPTH_BUFFER: Int
        get() = TODO("Not yet implemented")
}

actual object ExpectShaderType {
    actual val VERTEX_SHADER: Int
        get() = TODO("Not yet implemented")
    actual val FRAGMENT_SHADER: Int
        get() = TODO("Not yet implemented")
}

actual object ExpectBufferUsageType {
    actual val STATIC_DRAW: Int
        get() = TODO("Not yet implemented")
}

actual object ExpectDrawModes {
    actual val TRIANGLES: Int
        get() = TODO("Not yet implemented")
    actual val LINES: Int
        get() = TODO("Not yet implemented")
}

actual object ExpectCullModeType {
    actual val FRONT: Int
        get() = TODO("Not yet implemented")
    actual val BACK: Int
        get() = TODO("Not yet implemented")
    actual val FRONT_AND_BACK: Int
        get() = TODO("Not yet implemented")
}
