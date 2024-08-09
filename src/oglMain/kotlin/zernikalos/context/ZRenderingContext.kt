/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context

import kotlinx.serialization.SerialName
import zernikalos.components.material.ZBitmap
import zernikalos.ui.ZSurfaceView
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

expect class ZGLRenderingContext(surfaceView: ZSurfaceView): ZRenderingContext {

    override fun initWithSurfaceView(surfaceView: ZSurfaceView)

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

    fun getAttribLocation(program: GLWrap, attrName: String): Int

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

@JsExport
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

enum class Enabler(val value: Int) {
    DEPTH_TEST(ExpectEnabler.DEPTH_TEST)
}

@JsExport
enum class BufferUsageType(val value: Int) {
    @SerialName("static-draw")
    STATIC_DRAW(ExpectBufferUsageType.STATIC_DRAW)
}

enum class BufferBit(val value: Int) {
    COLOR_BUFFER(ExpectBufferBit.COLOR_BUFFER),
    DEPTH_BUFFER(ExpectBufferBit.DEPTH_BUFFER)
}

enum class CullModeType(val value: Int) {
    FRONT(ExpectCullModeType.FRONT),
    BACK(ExpectCullModeType.BACK),
    FRONT_AND_BACK(ExpectCullModeType.FRONT_AND_BACK)
}

enum class DrawModes(val value: Int) {
    TRIANGLES(ExpectDrawModes.TRIANGLES),
    LINES(ExpectDrawModes.LINES)
}

expect object ExpectEnabler {
    val DEPTH_TEST: Int
}

expect object ExpectBufferBit {
    val COLOR_BUFFER: Int
    val DEPTH_BUFFER: Int
}

expect object ExpectShaderType {
    val VERTEX_SHADER: Int
    val FRAGMENT_SHADER: Int
}

expect object ExpectBufferUsageType {
    val STATIC_DRAW: Int
}

expect object ExpectDrawModes {
    val TRIANGLES: Int
    val LINES: Int
}

expect object ExpectCullModeType {
    val FRONT: Int
    val BACK: Int
    val FRONT_AND_BACK: Int
}