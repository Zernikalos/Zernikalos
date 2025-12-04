/*
 *
 *  * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package zernikalos.components.mesh

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.GPUVertexAttribute
import zernikalos.context.webgpu.GPUVertexBufferLayout
import zernikalos.context.webgpu.GPUVertexStepMode

actual class ZBufferRenderer actual constructor(ctx: ZRenderingContext, private val data: ZBufferData) : ZComponentRenderer(ctx) {



    actual override fun initialize() {
        if (!data.content.isInitialized) {
            data.content.initialize(ctx)
            data.content.renderer.initializeAs(data.attributeId.id, data.isIndexBuffer)
        }
    }

    fun createLayout(): GPUVertexBufferLayout {
        // Use the stride from data, which includes interleaved stride when buffers are interleaved
        // When stride is 0, it means tightly packed, so use element size
        val strideBytes = if (data.stride == 0) data.dataType.byteSize else data.stride
        
        return GPUVertexBufferLayout(
            attributes = arrayOf(
                GPUVertexAttribute(
                    format = typeToWebGpuType(data.dataType),
                    offset = data.offset,
                    shaderLocation = data.attributeId.id
                )
            ),
            arrayStride = strideBytes,
            stepMode = GPUVertexStepMode.VERTEX
        )
    }

    actual override fun bind() {
        // In WebGPU, binding is typically done during render pass setup
        // This method might be a no-op or used for specific binding requirements
        ctx as ZWebGPURenderingContext

        ctx.renderPass?.setVertexBuffer(data.attributeId.id, data.content.renderer.wgpuBuffer)
    }

    fun bindIndexBuffer() {
        ctx as ZWebGPURenderingContext
        ctx.renderPass?.setIndexBuffer(data.content.renderer.wgpuBuffer, "uint16")
    }

    actual override fun unbind() {
        // Typically not needed in WebGPU
    }
}
