package zernikalos

import android.opengl.GLES30

actual object OglDataTypes {
    actual val BYTE: Int = GLES30.GL_BYTE
    actual val UNSIGNED_BYTE: Int = GLES30.GL_UNSIGNED_BYTE
    actual val INT: Int = GLES30.GL_INT
    actual val UNSIGNED_INT: Int = GLES30.GL_UNSIGNED_INT
    actual val SHORT: Int = GLES30.GL_SHORT
    actual val UNSIGNED_SHORT: Int = GLES30.GL_UNSIGNED_SHORT
    actual val FLOAT: Int = GLES30.GL_FLOAT
    actual val DOUBLE: Int = GLES30.GL_FLOAT

    actual val MAT4: Int = GLES30.GL_FLOAT_MAT4
}