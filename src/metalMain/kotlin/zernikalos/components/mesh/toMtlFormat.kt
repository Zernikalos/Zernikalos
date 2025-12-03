package zernikalos.components.mesh

import platform.Metal.*
import zernikalos.ZDataType
import zernikalos.ZTypes

fun toMtlFormat(dataType: ZDataType): MTLVertexFormat {
    return when (dataType) {
        ZTypes.BYTE -> MTLVertexFormatChar
        ZTypes.BYTE2 -> MTLVertexFormatChar2
        ZTypes.BYTE3 -> MTLVertexFormatChar3
        ZTypes.BYTE4 -> MTLVertexFormatChar4

        ZTypes.UBYTE -> MTLVertexFormatUChar
        ZTypes.UBYTE2 -> MTLVertexFormatUChar2
        ZTypes.UBYTE3 -> MTLVertexFormatUChar3
        ZTypes.UBYTE4 -> MTLVertexFormatUChar4

        ZTypes.INT -> MTLVertexFormatInt
        ZTypes.INT2 -> MTLVertexFormatInt2
        ZTypes.INT3 -> MTLVertexFormatInt3
        ZTypes.INT4 -> MTLVertexFormatInt4

        ZTypes.UINT -> MTLVertexFormatUInt
        ZTypes.UINT2 -> MTLVertexFormatUInt2
        ZTypes.UINT3 -> MTLVertexFormatUInt3
        ZTypes.UINT4 -> MTLVertexFormatUInt4

        ZTypes.USHORT -> MTLVertexFormatUShort
        ZTypes.USHORT2 -> MTLVertexFormatUShort2
        ZTypes.USHORT3 -> MTLVertexFormatUShort3
        ZTypes.USHORT4 -> MTLVertexFormatUShort4

        ZTypes.SHORT -> MTLVertexFormatShort
        ZTypes.SHORT2 -> MTLVertexFormatShort2
        ZTypes.SHORT3 -> MTLVertexFormatShort3
        ZTypes.SHORT4 -> MTLVertexFormatShort4

        ZTypes.FLOAT -> MTLVertexFormatFloat
        ZTypes.VEC2F -> MTLVertexFormatFloat2
        ZTypes.VEC3F -> MTLVertexFormatFloat3
        ZTypes.VEC4F -> MTLVertexFormatFloat4

        else -> 0u
    }
}
