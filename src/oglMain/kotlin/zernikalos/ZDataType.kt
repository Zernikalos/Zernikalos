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
        ZDataType.VEC2 -> TODO()
        ZDataType.VEC3 -> TODO()
        ZDataType.VEC4 -> TODO()
        ZDataType.MAT2F -> TODO()
        ZDataType.MAT3F -> TODO()
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

    val MAT4: Int
    val TEXTURE: Int
}