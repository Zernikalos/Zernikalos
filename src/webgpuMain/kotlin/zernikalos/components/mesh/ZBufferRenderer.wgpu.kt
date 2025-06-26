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

import kotlinx.serialization.Transient
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.GPUBuffer
import zernikalos.context.webgpu.GPUBufferUsage

actual class ZBufferRenderer actual constructor(ctx: ZRenderingContext, private val data: ZBufferData) : ZComponentRenderer(ctx) {
    @Transient
    lateinit var wgpuBuffer: GPUBuffer

    val usage: Int
    get() {
        return if (data.isIndexBuffer)
            GPUBufferUsage.INDEX or GPUBufferUsage.COPY_DST
        else
            GPUBufferUsage.VERTEX or GPUBufferUsage.COPY_DST
    }

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        // Each buffer will create a different GPUBuffer instance
        wgpuBuffer = ctx.device.createBuffer(
            data.dataArray.size * data.dataType.byteSize,
            usage,
            false,
            "${data.name}Buffer"
        )
        ctx.queue.writeBuffer(wgpuBuffer, 0, data.dataArray)
    }

    actual override fun bind() {
        // In WebGPU, binding is typically done during render pass setup
        // This method might be a no-op or used for specific binding requirements
    }

    actual override fun unbind() {
        // Typically not needed in WebGPU
    }
}
