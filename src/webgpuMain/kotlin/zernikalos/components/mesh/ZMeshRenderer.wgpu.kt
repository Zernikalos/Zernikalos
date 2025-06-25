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
    ZTypes.USHORT -> GPUVertexFormat.UINT16
    ZTypes.SHORT -> GPUVertexFormat.SINT16
    ZTypes.USHORT4 -> GPUVertexFormat.UINT16X4
    ZTypes.FLOAT -> GPUVertexFormat.FLOAT32
    ZTypes.VEC2F -> GPUVertexFormat.FLOAT32X2
    ZTypes.VEC3F -> GPUVertexFormat.FLOAT32X3
    ZTypes.VEC4F -> GPUVertexFormat.FLOAT32X4
    else -> throw Error("Unsupported type: $type")
}
