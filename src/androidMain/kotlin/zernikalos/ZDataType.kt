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

    actual val VEC2: Int = GLES30.GL_FLOAT_VEC2
    actual val VEC3: Int = GLES30.GL_FLOAT_VEC3
    actual val VEC4: Int = GLES30.GL_FLOAT_VEC4
    actual val MAT2: Int = GLES30.GL_FLOAT_MAT2
    actual val MAT3: Int = GLES30.GL_FLOAT_MAT3
    actual val MAT4: Int = GLES30.GL_FLOAT_MAT4

    actual val TEXTURE: Int = GLES30.GL_INT_SAMPLER_2D

}