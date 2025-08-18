/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context

import android.opengl.GLES30
import android.opengl.GLUtils
import zernikalos.components.material.ZBitmap
import zernikalos.ui.ZSurfaceView
import java.nio.ByteBuffer

actual class ZGLRenderingContext actual constructor(val surfaceView: ZSurfaceView): ZRenderingContext {

    actual override fun initWithSurfaceView(surfaceView: ZSurfaceView) {
    }

    actual fun enable(feat: Int) {
        GLES30.glEnable(feat)
    }

    actual fun clearColor(r: Float, g: Float, b: Float, a: Float) {
        GLES30.glClearColor(r,g, b, a)
    }

    actual fun viewport(top: Int, left: Int, width: Int, height: Int) {
        GLES30.glViewport(top, left, width, height)
    }

    actual fun cullFace(mode: Int) {
        GLES30.glCullFace(mode)
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

    actual fun bindAttribLocation(program: GLWrap, index: Int, attrName: String) {
        GLES30.glBindAttribLocation(program.id as Int, index, attrName)
    }

    actual fun getAttribLocation(program: GLWrap, attrName: String): Int {
        return GLES30.glGetAttribLocation(program.id as Int, attrName)
    }

    actual fun getUniformLocation(program: GLWrap, uniformName: String): GLWrap {
        val id = GLES30.glGetUniformLocation(program.id as Int, uniformName)
        return GLWrap(id)
    }

    actual fun getUniformBlockIndex(program: GLWrap, uniformBlockName: String): GLWrap {
        val idx = GLES30.glGetUniformBlockIndex(program.id as Int, uniformBlockName)
        return GLWrap(idx)
    }

    actual fun uniformBlockBinding(program: GLWrap, uniformBlockIndex: GLWrap, uniformBlockBinding: Int) {
        GLES30.glUniformBlockBinding(program.id as Int, uniformBlockIndex.id as Int, uniformBlockBinding)
    }

    actual fun bindBufferBase(targetType: BufferTargetType, uniformBlockBinding: Int, uniformBufferObject: GLWrap) {
        GLES30.glBindBufferBase(targetType.value, uniformBlockBinding, uniformBufferObject.id as Int)
    }

    actual fun uniformMatrix4fv(uniform: GLWrap, count: Int, transpose: Boolean, values: FloatArray) {
        GLES30.glUniformMatrix4fv(uniform.id as Int, count, transpose, values, 0)
    }

    actual fun createBuffer(): GLWrap {
        val buff = IntArray(1)
        GLES30.glGenBuffers(1, buff, 0)
        val id = buff[0]
        return GLWrap(id)
    }

    actual fun bindBuffer(targetType: BufferTargetType, buffer: GLWrap) {
        GLES30.glBindBuffer(targetType.value, buffer.id as Int)
    }

    actual fun bufferData(targetType: BufferTargetType, dataArray: ByteArray, usageType: BufferUsageType) {
        val buff = ByteBuffer.wrap(dataArray)
        GLES30.glBufferData(targetType.value, dataArray.size, buff, usageType.value)
    }

    actual fun bufferData(targetType: BufferTargetType, byteSize: Int, usageType: BufferUsageType) {
        GLES30.glBufferData(targetType.value, byteSize, null, usageType.value)
    }

    actual fun enableVertexAttrib(index: Int) {
        GLES30.glEnableVertexAttribArray(index)
    }

    actual fun enableVertexAttribArray(index: Int) {
        GLES30.glEnableVertexAttribArray(index)
    }

    actual fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) {
        GLES30.glVertexAttribPointer(index, size, type, normalized, stride, offset)
    }

    actual fun createVertexArray(): GLWrap {
        val buff = IntArray(1)
        GLES30.glGenVertexArrays(1, buff, 0)
        val id = buff[0]
        return GLWrap(id)
    }

    actual fun bindVertexArray(vao: GLWrap?) {
        if (vao == null)
            GLES30.glBindVertexArray(0)
        else {
            GLES30.glBindVertexArray(vao.id as Int)
        }
    }

    actual fun drawArrays(mode: Int, first: Int, count: Int) {
        GLES30.glDrawArrays(mode, first, count)
    }

    actual fun drawElements(mode: Int, count: Int, type: Int, offset: Int) {
        GLES30.glDrawElements(mode, count, type, offset)
    }

    /** Textures **/
    actual fun genTexture(): GLWrap {
        val buff = IntArray(1)
        GLES30.glGenTextures(1, buff, 0)
        val id = buff[0]
        return GLWrap(id)
    }

    actual fun activeTexture() {
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
    }

    actual fun bindTexture(texture: GLWrap) {
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture.id as Int)
    }

    actual fun texParameterMin() {
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST)
    }

    actual fun texParameterMag() {
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST)
    }

    actual fun texImage2D(bitmap: ZBitmap) {
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap.nativeBitmap, 0)
    }

}

actual object ExpectEnabler {
    actual val DEPTH_TEST: Int = GLES30.GL_DEPTH_TEST
}

actual object ExpectBufferTargetType {
    actual val ARRAY_BUFFER: Int = GLES30.GL_ARRAY_BUFFER
    actual val ELEMENT_ARRAY_BUFFER: Int = GLES30.GL_ELEMENT_ARRAY_BUFFER
    actual val UNIFORM_BUFFER: Int = GLES30.GL_UNIFORM_BUFFER
}

actual object ExpectBufferUsageType {
    actual val STATIC_DRAW: Int = GLES30.GL_STATIC_DRAW
    actual val DYNAMIC_DRAW: Int = GLES30.GL_DYNAMIC_DRAW
}

actual object ExpectBufferBit {
    actual val COLOR_BUFFER: Int = GLES30.GL_COLOR_BUFFER_BIT
    actual val DEPTH_BUFFER: Int = GLES30.GL_DEPTH_BUFFER_BIT
}

actual object ExpectShaderType {
    actual val VERTEX_SHADER: Int = GLES30.GL_VERTEX_SHADER
    actual val FRAGMENT_SHADER: Int = GLES30.GL_FRAGMENT_SHADER
}

actual object ExpectDrawModes {
    actual val TRIANGLES: Int = GLES30.GL_TRIANGLES
    actual val LINES: Int = GLES30.GL_LINES
}

actual object ExpectCullModeType {
    actual val FRONT: Int = GLES30.GL_FRONT
    actual val BACK: Int = GLES30.GL_BACK
    actual val FRONT_AND_BACK: Int = GLES30.GL_FRONT_AND_BACK
}
