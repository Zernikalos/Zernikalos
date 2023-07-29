package zernikalos

import org.khronos.webgl.WebGLRenderingContext

actual object OglDataTypes {
    actual val UNSIGNED_BYTE: Int = WebGLRenderingContext.UNSIGNED_BYTE
    actual val BYTE: Int = WebGLRenderingContext.BYTE
    actual val INT: Int = WebGLRenderingContext.INT
    actual val UNSIGNED_INT: Int = WebGLRenderingContext.UNSIGNED_INT
    actual val SHORT: Int = WebGLRenderingContext.SHORT
    actual val UNSIGNED_SHORT: Int = WebGLRenderingContext.UNSIGNED_SHORT
    actual val FLOAT: Int = WebGLRenderingContext.FLOAT
    actual val DOUBLE: Int = WebGLRenderingContext.FLOAT

    actual val MAT4: Int = WebGLRenderingContext.FLOAT_MAT4
}