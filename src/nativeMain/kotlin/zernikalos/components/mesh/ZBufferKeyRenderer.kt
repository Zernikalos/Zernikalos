package zernikalos.components.mesh

import platform.Metal.*
import zernikalos.ZRenderingContext
import zernikalos.components.ZComponentRender

actual class ZBufferKeyRenderer actual constructor() : ZComponentRender<ZBufferKeyData> {

    lateinit var attributeDescriptor: MTLVertexAttributeDescriptor
    lateinit var layoutDescriptor: MTLVertexBufferLayoutDescriptor

    actual override fun initialize(ctx: ZRenderingContext, data: ZBufferKeyData) {
        attributeDescriptor = MTLVertexAttributeDescriptor()
        attributeDescriptor.offset = data.offset.toULong()
        attributeDescriptor.bufferIndex = data.id.toULong()
        attributeDescriptor.format = MTLVertexFormatFloat3

        layoutDescriptor = MTLVertexBufferLayoutDescriptor()
        layoutDescriptor.stride = 12u
        layoutDescriptor.stepRate = 1u
        layoutDescriptor.stepFunction = MTLStepFunctionPerVertex
    }

}