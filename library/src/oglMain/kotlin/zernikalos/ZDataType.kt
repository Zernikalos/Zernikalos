/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos

fun toOglType(t: ZDataType): Int {
    val value = when (t) {
        ZTypes.BYTE -> OglDataTypes.BYTE
        ZTypes.UBYTE -> OglDataTypes.UNSIGNED_BYTE
        ZTypes.SHORT -> OglDataTypes.SHORT
        ZTypes.USHORT -> OglDataTypes.UNSIGNED_SHORT
        ZTypes.INT -> OglDataTypes.INT
        ZTypes.UINT -> OglDataTypes.UNSIGNED_INT
        ZTypes.FLOAT -> OglDataTypes.FLOAT
        ZTypes.DOUBLE -> OglDataTypes.DOUBLE
        ZTypes.VEC2F -> OglDataTypes.VEC2
        ZTypes.VEC3F -> OglDataTypes.VEC3
        ZTypes.VEC4F -> OglDataTypes.VEC4
        ZTypes.MAT2F -> OglDataTypes.MAT2
        ZTypes.MAT3F -> OglDataTypes.MAT3
        ZTypes.MAT4F -> OglDataTypes.MAT4
        ZTypes.TEXTURE -> OglDataTypes.TEXTURE
        else -> 0
    }
    return value
}

fun toOglBaseType(t: ZDataType): Int {
    val value = when (t.type) {
        ZBaseType.NONE -> 0
        ZBaseType.BYTE -> OglDataTypes.BYTE
        ZBaseType.UNSIGNED_BYTE -> OglDataTypes.UNSIGNED_BYTE
        ZBaseType.SHORT -> OglDataTypes.SHORT
        ZBaseType.UNSIGNED_SHORT -> OglDataTypes.UNSIGNED_SHORT
        ZBaseType.INT -> OglDataTypes.INT
        ZBaseType.UNSIGNED_INT -> OglDataTypes.UNSIGNED_INT
        ZBaseType.FLOAT -> OglDataTypes.FLOAT
        ZBaseType.DOUBLE -> OglDataTypes.DOUBLE
        ZBaseType.TEXTURE -> OglDataTypes.TEXTURE
    }
    return value
}

expect object OglDataTypes {
    val BYTE: Int
    val UNSIGNED_BYTE: Int
    val INT: Int
    val UNSIGNED_INT: Int
    val SHORT: Int
    val UNSIGNED_SHORT: Int
    val FLOAT: Int
    val DOUBLE: Int

    val VEC2: Int
    val VEC3: Int
    val VEC4: Int
    val MAT2: Int
    val MAT3: Int
    val MAT4: Int

    val TEXTURE: Int
}