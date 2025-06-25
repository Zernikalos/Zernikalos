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

        vertexBuffersLayout = arrayOf<GPUVertexBufferLayout?>(
            null,
            *buildVertexBuffersLayout()
        )
    }

    private fun buildVertexBuffersLayout(): Array<GPUVertexBufferLayout> {
        val attributes = data.buffers.values
            // TODO: Remove this, this is hardcoded just for testing
            .filter { buff -> buff.name == "position" }
            .filter {buff -> buff.enabled && !buff.isIndexBuffer }.map { buff ->
            GPUVertexBufferLayout(
                attributes = arrayOf<GPUVertexAttribute>(GPUVertexAttribute(
                    format = typeToWebGpuType(buff.dataType),
                    offset = buff.offset,
                    shaderLocation = buff.attributeId.id
                )),
                arrayStride = buff.dataType.byteSize,
                stepMode = GPUVertexStepMode.VERTEX
            )

        }
        return attributes.toTypedArray()
    }

    override fun bind() {
        ctx as ZWebGPURenderingContext
        val position = data.buffers["position"]!!
        val indices = data.indexBuffer!!

        ctx.renderPass?.setVertexBuffer(position.id, position.renderer.wgpuBuffer)
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
