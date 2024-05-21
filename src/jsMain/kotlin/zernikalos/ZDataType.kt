/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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

    actual val VEC2: Int = WebGLRenderingContext.FLOAT_VEC2
    actual val VEC3: Int = WebGLRenderingContext.FLOAT_VEC3
    actual val VEC4: Int = WebGLRenderingContext.FLOAT_VEC4
    actual val MAT2: Int = WebGLRenderingContext.FLOAT_MAT2
    actual val MAT3: Int = WebGLRenderingContext.FLOAT_MAT3
    actual val MAT4: Int = WebGLRenderingContext.FLOAT_MAT4

    actual val TEXTURE: Int = WebGLRenderingContext.TEXTURE_2D
}