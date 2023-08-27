package zernikalos

fun toOglType(t: ZDataType): Int {
    val value = when (t) {
        ZDataType.BYTE -> OglDataTypes.BYTE
        ZDataType.UNSIGNED_BYTE -> OglDataTypes.UNSIGNED_BYTE
        ZDataType.SHORT -> OglDataTypes.SHORT
        ZDataType.UNSIGNED_SHORT -> OglDataTypes.UNSIGNED_SHORT
        ZDataType.INT -> OglDataTypes.INT
        ZDataType.UNSIGNED_INT -> OglDataTypes.UNSIGNED_INT
        ZDataType.FLOAT -> OglDataTypes.FLOAT
        ZDataType.DOUBLE -> OglDataTypes.DOUBLE
        ZDataType.VEC2 -> OglDataTypes.VEC2
        ZDataType.VEC3 -> OglDataTypes.VEC3
        ZDataType.VEC4 -> OglDataTypes.VEC4
        ZDataType.MAT2F -> OglDataTypes.MAT2
        ZDataType.MAT3F -> OglDataTypes.MAT3
        ZDataType.MAT4F -> OglDataTypes.MAT4
        ZDataType.TEXTURE -> OglDataTypes.TEXTURE
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