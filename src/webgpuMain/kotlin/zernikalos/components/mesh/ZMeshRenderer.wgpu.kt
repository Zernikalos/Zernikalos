package zernikalos.components.mesh

import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.GPUVertexAttribute
import zernikalos.context.webgpu.GPUVertexBufferLayout
import zernikalos.context.webgpu.GPUVertexFormat
import zernikalos.context.webgpu.GPUVertexStepMode

actual class ZMeshRenderer actual constructor(ctx: ZRenderingContext, private val data: ZMeshData): ZComponentRenderer(ctx) {

    var vertexBuffersLayout: Array<GPUVertexBufferLayout?> = arrayOf()

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        data.buffers.values.filter {buff ->
            buff.enabled
        }.forEach { buff ->
            buff.initialize(ctx)
        }

        vertexBuffersLayout = buildVertexBuffersLayout()
    }

    private fun buildVertexBuffersLayout(): Array<GPUVertexBufferLayout?> {
        val enabledVertexBuffers = data.buffers.values
            .filter { buff -> buff.enabled && !buff.isIndexBuffer }

        // Determine the highest attribute index to size the array correctly
        val maxIndex = enabledVertexBuffers.maxOf { it.attributeId.id }
        val attributes: Array<GPUVertexBufferLayout?> = arrayOfNulls(maxIndex + 1)

        // Populate the array so that attributes[i] corresponds to attributeId == i
        enabledVertexBuffers.forEach { buff ->
            attributes[buff.attributeId.id] = GPUVertexBufferLayout(
                attributes = arrayOf(
                    GPUVertexAttribute(
                        format = typeToWebGpuType(buff.dataType),
                        offset = buff.offset,
                        shaderLocation = buff.attributeId.id
                    )
                ),
                arrayStride = buff.dataType.byteSize,
                stepMode = GPUVertexStepMode.VERTEX
            )
        }

        return attributes
    }

    override fun bind() {
        ctx as ZWebGPURenderingContext
        val indices = data.indexBuffer!!

        data.buffers.values.filter {buff -> buff.enabled && !buff.isIndexBuffer }
        .sortedBy { it.id } // Use the same consistent order
        .forEach{ buff -> // Use index for the slot
            ctx.renderPass?.setVertexBuffer(buff.attributeId.id, buff.renderer.wgpuBuffer)
        }
        ctx.renderPass?.setIndexBuffer(indices.renderer.wgpuBuffer, "uint16")
    }

    actual override fun render() {
        ctx as ZWebGPURenderingContext

        val indices = data.indexBuffer!!
        ctx.renderPass?.drawIndexed(indices.count)
    }
}

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
