package zernikalos.components.mesh

import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.context.webgpu.GPUVertexFormat

// TODO: Fulfill this
fun typeToWebGpuType(type: ZDataType): String = when (type) {
    // 8-bit signed integer
    ZTypes.BYTE2 -> GPUVertexFormat.SINT8X2
    ZTypes.BYTE4 -> GPUVertexFormat.SINT8X4

    // 8-bit unsigned integer
    ZTypes.UBYTE2 -> GPUVertexFormat.UINT8X2
    ZTypes.UBYTE4 -> GPUVertexFormat.UINT8X4

    // 16-bit signed integer
    ZTypes.SHORT -> GPUVertexFormat.SINT16
    ZTypes.SHORT2 -> GPUVertexFormat.SINT16X2
    ZTypes.SHORT4 -> GPUVertexFormat.SINT16X4

    // 16-bit unsigned integer
    ZTypes.USHORT -> GPUVertexFormat.UINT16
    ZTypes.USHORT2 -> GPUVertexFormat.UINT16X2
    ZTypes.USHORT4 -> GPUVertexFormat.UINT16X4

    // 32-bit signed integer
    ZTypes.INT -> GPUVertexFormat.SINT32
    ZTypes.INT2 -> GPUVertexFormat.SINT32X2
    ZTypes.INT4 -> GPUVertexFormat.SINT32X4

    // 32-bit unsigned integer
    ZTypes.UINT -> GPUVertexFormat.UINT32
    ZTypes.UINT2 -> GPUVertexFormat.UINT32X2
    ZTypes.UINT4 -> GPUVertexFormat.UINT32X4

    // 32-bit float
    ZTypes.FLOAT -> GPUVertexFormat.FLOAT32
    ZTypes.VEC2F -> GPUVertexFormat.FLOAT32X2
    ZTypes.VEC3F -> GPUVertexFormat.FLOAT32X3
    ZTypes.VEC4F -> GPUVertexFormat.FLOAT32X4

    // Types not supported as vertex formats in WebGPU (e.g., matrices, doubles, x3 integer vectors)
    // are handled elsewhere or should be converted/padded.
    else -> error("Unsupported or invalid ZType for WebGPU vertex format: $type")
}
